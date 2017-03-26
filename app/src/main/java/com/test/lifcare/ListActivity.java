package com.test.lifcare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.test.lifcare.adapter.ListAdapter;
import com.test.lifcare.custom.SimpleDividerItemDecoration;
import com.test.lifcare.model.Data;
import com.test.lifcare.model.Phone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.READ_CONTACTS;


public class ListActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = ListActivity.class.getSimpleName();

    private static final Comparator<Data> ALPHABETICAL_COMPARATOR = new Comparator<Data>() {
        @Override
        public int compare(Data a, Data b) {
            return a.getName().compareTo(b.getName());
        }
    };
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView mRecyclerView;
    private FastScroller fastScroller;
    private ListAdapter listAdapter;
    private List<Data> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDataList = new ArrayList<>();
        initialiseView();
        setAdapter();
        populateAutoComplete();
    }

    private void initialiseView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.parent_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        fastScroller = (FastScroller) findViewById(R.id.fastscroll);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private void setAdapter() {
        listAdapter = new ListAdapter(this, ALPHABETICAL_COMPARATOR);
        mRecyclerView.setAdapter(listAdapter);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(coordinatorLayout, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        final String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.DELETED};

        @SuppressWarnings("deprecation")
        final CursorLoader rawContacts = new CursorLoader(this, ContactsContract.RawContacts.CONTENT_URI, projection, null, null, null);
        return rawContacts;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        final int contactIdColumnIndex = cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
        final int deletedColumnIndex = (cursor.getColumnIndex(ContactsContract.RawContacts.DELETED));

        Set<String> ids = new HashSet<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                final int contactId = cursor.getInt(contactIdColumnIndex);
                final boolean deleted = (cursor.getInt(deletedColumnIndex) == 1);
                if (!ids.contains(String.valueOf(contactId))) {
                    ids.add(String.valueOf(contactId));
                    Data data = new Data();
                    Log.d(TAG, String.valueOf(contactId));
                    data.setName(getName(contactId));
                    data.setPhoneId(String.valueOf(contactId));
                    data.setmBitmap(getPhoto(contactId) != null ? getPhoto(contactId) : null);
                    data.setmPhone(getPhoneNumber(contactId));
                    mDataList.add(data);
                }
                cursor.moveToNext();
            }
            listAdapter.add(mDataList);
//            listAdapter.notifyDataSetChanged();
        }
    }

    private String getName(int contactId) {
        String name = "";
        final String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

        final Cursor contact = managedQuery(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(contactId)}, null);

        if (contact.moveToFirst()) {
            name = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact.close();
        }
        contact.close();
        return name;

    }

    private Bitmap getPhoto(int contactId) {
        Bitmap photo = null;
        final String[] projection = new String[]{ContactsContract.Contacts.PHOTO_ID};

        final Cursor contact = managedQuery(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(contactId)}, null);

        if (contact.moveToFirst()) {
            final String photoId = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            if (photoId != null) {
                photo = getBitmap(photoId);
            } else {
                photo = null;
            }
        }
        contact.close();

        return photo;
    }

    private List<Phone> getPhoneNumber(int contactId) {

        String phoneNumber = "";
        List<Phone> mPhone = new ArrayList<>();
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE,};
        final Cursor phone = managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
            while (!phone.isAfterLast()) {
                Phone _phone = new Phone();
                _phone.setPhone(phone.getString(contactNumberColumnIndex));
                phoneNumber = phoneNumber + phone.getString(contactNumberColumnIndex) + ";";
                phone.moveToNext();
                mPhone.add(_phone);
            }

        }
        phone.close();
        return mPhone;
    }

    private Bitmap getBitmap(String photoId) {
        final Cursor photo = managedQuery(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, ContactsContract.Data._ID + "=?", new String[]{photoId}, null);

        final Bitmap photoBitmap;
        if (photo.moveToFirst()) {
            byte[] photoBlob = photo.getBlob(photo.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
            photoBitmap = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
        } else {
            photoBitmap = null;
        }
        photo.close();
        return photoBitmap;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


}

