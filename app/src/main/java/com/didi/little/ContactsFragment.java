package com.didi.little;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
public class ContactsFragment extends Fragment {
    private View view_root;
    private ListView listView_users;
    private TextView textView_emptyinfo;
    private List<UserModel> totallList = null;
    private SidebarView sidebarView_main;
    private TextView textView_dialog;
    private MySortAdapter adapter = null;
    private FloatingActionButton floatbtn_add;

    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    private static final int PHONES_NUMBER_INDEX = 1;

    private static final int PHONES_PHOTO_ID_INDEX = 2;

    private static final int PHONES_CONTACT_ID_INDEX = 3;

    private ArrayList<String> mContactsName = new ArrayList<String>();

    private ArrayList<String> mContactsNumber = new ArrayList<String>();

    private  final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID,ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
    private PopupWindow pw_addnumber;
    private View view_addphone;
    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_root=inflater.inflate(R.layout.gamefragment,null);
        textView_dialog=(TextView)view_root.findViewById(R.id.textView_dialog);

        new GetPhoneNum().execute();

        return view_root;
    }
    private class GetPhoneNum extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView_dialog.setVisibility(View.VISIBLE);
            textView_dialog.setText("加载中...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    mContactsName.add(contactName);
                    mContactsNumber.add(phoneNumber);
                }
                phoneCursor.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            textView_dialog.setVisibility(View.GONE);
            initView();
            popRecoringWin();
            super.onPostExecute(aVoid);
        }
    }
    public void initView() {
        floatbtn_add=(FloatingActionButton)view_root .findViewById(R.id.floatbtn_add);
        listView_users=(ListView)view_root.findViewById(R.id.listView_users);
        textView_emptyinfo=(TextView)view_root.findViewById(R.id.textView_emptyinfo);
        sidebarView_main=(SidebarView)view_root.findViewById(R.id.sidebarView_main);
        sidebarView_main.setTextView(textView_dialog);
        totallList = new ArrayList<UserModel>();
        totallList = getUserList();

        Collections.sort(totallList, new Comparator<UserModel>() {
            @Override
            public int compare(UserModel lhs, UserModel rhs) {
                if (lhs.getFirstLetter().equals("#")) {
                    return 1;
                } else if (rhs.getFirstLetter().equals("#")) {
                    return -1;
                } else {
                    return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
                }
            }
        });

        adapter = new MySortAdapter(getActivity(), totallList);
        listView_users.setAdapter(adapter);
        listView_users.setEmptyView(textView_emptyinfo);

        listView_users.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                  TextView tv_name=(TextView)view.findViewById(R.id.textView_item_username);
                  TextView tv_number=(TextView)view.findViewById(R.id.textView_item_number);

                  Intent intent=new Intent(getActivity(),ScrollingActivity.class);
                  intent.putExtra("name",tv_name.getText().toString());
                  intent.putExtra("number",tv_number.getText().toString());
                  startActivity(intent);
//                TextView tv_name=(TextView)view.findViewById(R.id.textView_item_number);
//
//                //Toast.makeText(getActivity(), tv_name.getText().toString(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tv_name.getText().toString()));
//                startActivity(intent);
            }
        });

        sidebarView_main.setOnLetterClickedListener(new SidebarView.OnLetterClickedListener() {
            @Override
            public void onLetterClicked(String str) {
                int position = adapter.getPositionForSection(str
                        .charAt(0));
                listView_users.setSelection(position);
            }
        });
        floatbtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pw_addnumber.isShowing()){
                    pw_addnumber.dismiss();
                }else{
                    pw_addnumber.showAtLocation(view_addphone, Gravity.CENTER,0,0);
                }
            }
        });
    }
    private void popRecoringWin(){
        view_addphone=LayoutInflater.from(getContext()).inflate(R.layout.addphonenumber,null);
        final EditText edit_addname=(EditText)view_addphone.findViewById(R.id.editText_addname);
        final EditText edit_addphone=(EditText)view_addphone.findViewById(R.id.edit_addphone);
        Button btn_store=(Button)view_addphone.findViewById(R.id.btn_store);
        Button btn_canle=(Button)view_addphone.findViewById(R.id.btn_cancle);

        pw_addnumber=new PopupWindow(view_addphone, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,true);
        pw_addnumber.setAnimationStyle(android.support.v7.appcompat.R.style.Animation_AppCompat_Dialog);

        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_addname.getText().toString().isEmpty() || edit_addphone.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "不可以为空", Toast.LENGTH_SHORT).show();
                }else {
                    testAddContacts(edit_addname.getText().toString().trim(),edit_addphone.getText().toString().trim());
                    pw_addnumber.dismiss();
                    Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();

                    mContactsNumber.clear();
                    mContactsName.clear();
                    totallList = new ArrayList<UserModel>();
                    totallList = getUserList();

                    Collections.sort(totallList, new Comparator<UserModel>() {
                        @Override
                        public int compare(UserModel lhs, UserModel rhs) {
                            if (lhs.getFirstLetter().equals("#")) {
                                return 1;
                            } else if (rhs.getFirstLetter().equals("#")) {
                                return -1;
                            } else {
                                return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
                            }
                        }
                    });

                    adapter = new MySortAdapter(getActivity(), totallList);
                    listView_users.setAdapter(adapter);
                    listView_users.setEmptyView(textView_emptyinfo);

                }
            }
        });
        btn_canle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw_addnumber.dismiss();
            }
        });
    }

    public void testAddContacts(String name ,String phone){
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        long contactId = ContentUris.parseId(resolver.insert(uri, values));

        uri = Uri.parse("content://com.android.contacts/data");
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data2", name);
        resolver.insert(uri, values);

        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");
        values.put("data1", phone);
        resolver.insert(uri, values);
    }
    private List<UserModel> getUserList() {
        List<UserModel> list = new ArrayList<UserModel>();
        for (int i = 0; i < mContactsName.size(); i++) {
            UserModel userModel = new UserModel();
            String username = mContactsName.get(i);
            String usernumber=mContactsNumber.get(i);
            String pinyin = ChineseToPinyinHelper.getInstance().getPinyin(
                    username);
            String firstLetter = pinyin.substring(0, 1).toUpperCase();
            if (firstLetter.matches("[A-Z]")) {
                userModel.setFirstLetter(firstLetter);
            } else {
                userModel.setFirstLetter("#");
            }
            userModel.setUesrname(username);
            userModel.setUsernumber(usernumber);
            list.add(userModel);
        }
        return list;
    }
}
