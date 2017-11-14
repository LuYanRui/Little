package com.didi.little;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
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
    // 当前fragment view
    private View view_root;
    // 联系人列表
    private ListView listView_users;
    // 显示空信息
    private TextView textView_emptyinfo;
    // 装载listview
    private List<UserModel> totallList = null;
    //自定义列表view
    private SidebarView sidebarView_main;
    // 提示框
    private TextView textView_dialog;
    // 列表适配器
    private MySortAdapter adapter = null;
    // 添加联系人
    private FloatingActionButton floatbtn_add;

    //姓名索引
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    // 电话索引
    private static final int PHONES_NUMBER_INDEX = 1;
    // 姓名列表
    private ArrayList<String> mContactsName = new ArrayList<String>();
    //电话列表
    private ArrayList<String> mContactsNumber = new ArrayList<String>();
    // 姓名，电话字段
    private  final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID,ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
    // 添加联系人view提示框
    private PopupWindow pw_addnumber;
    // 添加联系人view
    private View view_addphone;
    // 构造函数
    public ContactsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 加载view
        view_root=inflater.inflate(R.layout.gamefragment,null);
        textView_dialog=(TextView)view_root.findViewById(R.id.textView_dialog);

        new GetPhoneNum().execute();

        return view_root;
    }

    /**
     * 异步获取联系人信息，避免主线程操作防止anr
     */
    private class GetPhoneNum extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView_dialog.setVisibility(View.VISIBLE);
            textView_dialog.setText("加载中...");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // 在数据库获取联系人电话，姓名等
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

    // 初始化组件
    public void initView() {
        floatbtn_add=(FloatingActionButton)view_root .findViewById(R.id.floatbtn_add);
        listView_users=(ListView)view_root.findViewById(R.id.listView_users);
        textView_emptyinfo=(TextView)view_root.findViewById(R.id.textView_emptyinfo);
        sidebarView_main=(SidebarView)view_root.findViewById(R.id.sidebarView_main);
        sidebarView_main.setTextView(textView_dialog);
        totallList = new ArrayList<UserModel>();
        totallList = getUserList();

        // 按照字母对联系人进行排序
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

        // 装载数据
        adapter = new MySortAdapter(getActivity(), totallList);
        listView_users.setAdapter(adapter);
        listView_users.setEmptyView(textView_emptyinfo);

        // 点击跳转到联系人详情页面
        listView_users.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                  TextView tv_name=(TextView)view.findViewById(R.id.textView_item_username);
                  TextView tv_number=(TextView)view.findViewById(R.id.textView_item_number);

                  // 传值 姓名 电话
                  Intent intent=new Intent(getActivity(),ScrollingActivity.class);
                  intent.putExtra("name",tv_name.getText().toString());
                  intent.putExtra("number",tv_number.getText().toString());
                  startActivity(intent);

            }
        });
         // 滑动点击事件
        sidebarView_main.setOnLetterClickedListener(new SidebarView.OnLetterClickedListener() {
            @Override
            public void onLetterClicked(String str) {
                int position = adapter.getPositionForSection(str
                        .charAt(0));
                listView_users.setSelection(position);
            }
        });
        // 添加联系人点击事件
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
    // 展开添加联系人页面
    private void popRecoringWin(){
        view_addphone=LayoutInflater.from(getContext()).inflate(R.layout.addphonenumber,null);
        final EditText edit_addname=(EditText)view_addphone.findViewById(R.id.editText_addname);
        final EditText edit_addphone=(EditText)view_addphone.findViewById(R.id.edit_addphone);
        Button btn_store=(Button)view_addphone.findViewById(R.id.btn_store);
        Button btn_canle=(Button)view_addphone.findViewById(R.id.btn_cancle);

        pw_addnumber=new PopupWindow(view_addphone, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,true);
        pw_addnumber.setAnimationStyle(android.support.v7.appcompat.R.style.Animation_AppCompat_Dialog);

        //保存联系人信息
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
        // 取消此次保存操作
        btn_canle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw_addnumber.dismiss();
            }
        });
    }

    /**
     * 添加联系人
     * @param name 联系人姓名
     * @param phone 联系人电话
     */
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
    // 装载联系人数据
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
