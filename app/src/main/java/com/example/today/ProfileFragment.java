package com.example.today;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

FirebaseAuth firebaseAuth ;
FirebaseUser user;
FirebaseDatabase firebaseDatabase;
DatabaseReference databaseReference;

StorageReference storageReference;

String storagePath = "Users_Profile_Cover_Imgs/";

ImageView avatarIv,coverIv;
TextView nameTV,emailTV,phoneTV;
FloatingActionButton fab;
ProgressDialog pd;

    private  static  final int CAMERA_REQUEST_CODE = 100;
    private  static  final int STORAGE_REQUEST_CODE = 200;
    private  static  final int IMAGE_PICK_GALLERY_CODE = 300;
    private  static  final int IMAGE_PICK_CAMERA_CODE = 400;
    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri;

    String profileOrCoverPhoto ;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

       firebaseAuth = FirebaseAuth.getInstance();
       user = firebaseAuth.getCurrentUser();
       firebaseDatabase = FirebaseDatabase.getInstance();
       databaseReference = firebaseDatabase.getReference("Users");
       storageReference = FirebaseStorage.getInstance().getReference();

       cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
       storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};

       avatarIv = view.findViewById(R.id.avatarIv);
        nameTV = view.findViewById(R.id.nameTV);
        emailTV = view.findViewById(R.id.emailTV);
        phoneTV = view.findViewById(R.id.phoneTV);
        coverIv = view.findViewById(R.id.coverIv);
        fab = view.findViewById(R.id.fab);

        pd = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ds.child("name").getValue();
                    String email = ""+ds.child("email").getValue();
                    String phone = ""+ds.child("phone").getValue();
                    String image = ""+ds.child("image").getValue();
                    String cover = ""+ds.child("cover").getValue();

                    nameTV.setText(name);
                    emailTV.setText(email);
                    phoneTV.setText(phone);
                    try{
                        Picasso.get().load(image).into(avatarIv);
                    }
                    catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.default_profile_pic).into(avatarIv);
                    }
                    try{
                        Picasso.get().load(cover).into(coverIv);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditProfileDiaog();
            }
        });

        return view;
    }

    private boolean checkCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1 ;
    }
    private void requestStoragePermission()
    {
        requestPermissions(storagePermissions , STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission()
    {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                == (PackageManager.PERMISSION_GRANTED);
    }
    private void requestCameraPermission()
    {
        requestPermissions(cameraPermissions , CAMERA_REQUEST_CODE);
    }


    private void ShowEditProfileDiaog() {
        String options[] = {"Edit Profile Picture","Edit Cover Photo","Edit Name","Edit Phone No."};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                 pd.setMessage("Updating Profile Picture");
                 profileOrCoverPhoto = "image";
                  showImagePicDialog();
                }
                else if(which == 1)
                {
                    pd.setMessage("Updating Cover Photo");
                    profileOrCoverPhoto = "cover";
                    showImagePicDialog();
                }
                else if(which == 2)
                {
                    pd.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name");
                }
                else if(which == 3)
                {

                    pd.setMessage("Updating Phone No.");
                    showNamePhoneUpdateDialog("phone");
                }

            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        EditText editText=new EditText(getActivity());
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              String value = editText.getText().toString().trim();
              if(!TextUtils.isEmpty(value))
              {
                  pd.show();
                  HashMap<String,Object> result = new HashMap<>();
                   result.put(key,value);

                   databaseReference.child(user.getUid()).updateChildren(result)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused)
                               {
                                 pd.dismiss();
                                 Toast.makeText(getActivity(),"updated...",Toast.LENGTH_SHORT).show();
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e)
                               {
                                 pd.dismiss();
                                   Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                           });
              }
              else
              {
                  Toast.makeText(getActivity(),"Please enter "+key, Toast.LENGTH_SHORT).show();
              }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();

    }

    private void showImagePicDialog() {
        String options[] = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upload Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
//                    pd.setMessage("updating Profile Picture");
//                    showImagePicDialog();
                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else{
                         pickFromCamera();
                    }

                } else if (which == 1) {
//                    pd.setMessage("updating Cover Photo");
                      if(!checkStoragePermission())
                      {
                          requestStoragePermission();
                      }
                      else
                      {
                          pickFromGallery();
                      }
                }
            }
        });
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE || requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean storageAccepted = false;
                boolean cameraAccepted = false;

                // Check permissions based on requestCode
                if (requestCode == CAMERA_REQUEST_CODE) {
                    cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                } else if (requestCode == STORAGE_REQUEST_CODE) {
                    storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                }

                // Check if both camera and storage permissions are granted
                if (cameraAccepted || storageAccepted) { // Use || instead of &&
                    if (requestCode == CAMERA_REQUEST_CODE) {
                        pickFromCamera();
                    } else {
                        pickFromGallery();
                    }
                } else {
                    String permission = requestCode == CAMERA_REQUEST_CODE ? "camera" : "storage";
                    Toast.makeText(getActivity(), "Please enable " + permission + " permission", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK)
        {
          if(requestCode == IMAGE_PICK_GALLERY_CODE)
          {
              image_uri = data.getData();
              uploadProfileCoverPhoto(image_uri);
          }
          if(requestCode == IMAGE_PICK_CAMERA_CODE)
          {
              uploadProfileCoverPhoto(image_uri);
          }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(final Uri uri) {

      String filePathAndName = storagePath+ "" +profileOrCoverPhoto+"_"+user.getUid();
      StorageReference storageReference2nd = storageReference.child(filePathAndName);
      storageReference2nd.putFile(uri)
              .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                  while(!uriTask.isSuccessful());
                  Uri downloadUri = uriTask.getResult();

                  if(uriTask.isSuccessful())
                  {
                      HashMap<String,Object> results = new HashMap<>();
                      results.put(profileOrCoverPhoto,downloadUri.toString());
                      databaseReference.child(user.getUid()).updateChildren(results)
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void unused) {
                                     pd.dismiss();
                                     Toast.makeText(getActivity(),"Image Updated...",Toast.LENGTH_SHORT).show();
                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                     pd.dismiss();
                                      Toast.makeText(getActivity(),"Error Updating Image...",Toast.LENGTH_SHORT).show();
                                  }
                              });
                  }else {
                      pd.dismiss();
                      Toast.makeText(getActivity(),"Some error occurred...",Toast.LENGTH_SHORT).show();

                  }

                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                   pd.dismiss();
                   Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                  }
              });
    }
    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent , IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private  void CheckUserStatus()
    {
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!=null)
        {
            //mProfileTv.setText(user.getEmail());
        }
        else {
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            CheckUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
