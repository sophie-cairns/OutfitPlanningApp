package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import uk.ac.aston.cs3mdd.outfitapp.R;

public class EditClothingFragment extends Fragment {
    private Button confirmButton;
    private Button cancelButton;
    private Button chooseImageButton;
    private ImageView imageView;
    private AutoCompleteTextView autoCompleteClothingTypeTextView;
    private AutoCompleteTextView autoCompleteColourTextView;
    private EditText brandEditText;
    private EditText tagsEditText;
    private ClothingDbViewModel clothingDbViewModel;
    private String type;
    private String colour;
    private ClothingItem clothingItem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_clothing, container, false);

        if (getArguments() != null) {
            clothingItem = (ClothingItem) getArguments().getSerializable("clothingItem");
        }

        confirmButton = view.findViewById(R.id.confirmButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        imageView = view.findViewById(R.id.imageView);
        imageView.setImageURI(clothingItem.getImage());
        chooseImageButton = view.findViewById(R.id.selectImageButton);
        autoCompleteClothingTypeTextView = view.findViewById(R.id.autoCompleteClothingTypeTextView);
        brandEditText = view.findViewById(R.id.editTextBrand);
        tagsEditText = view.findViewById(R.id.editTextTags);
        type = clothingItem.getType();
        autoCompleteClothingTypeTextView.setText(type); 
        autoCompleteColourTextView = view.findViewById(R.id.autoCompleteColourTextView);
        colour = clothingItem.getColour();
        autoCompleteColourTextView.setText(colour);
        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);


        String[] typesArray = getResources().getStringArray(R.array.clothing_types);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, typesArray);
        autoCompleteClothingTypeTextView.setAdapter(typeAdapter);

        String[] coloursArray = getResources().getStringArray(R.array.colours);
        ArrayAdapter<String> colourAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, coloursArray);
        autoCompleteColourTextView.setAdapter(colourAdapter);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToClothingFragment();
            }
        });

        autoCompleteClothingTypeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = (String) parent.getItemAtPosition(position);
            }

        });

        autoCompleteColourTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                colour = (String) parent.getItemAtPosition(position);
            }

        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = imageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                Uri imageUri = getImageUri(requireContext(), bitmap);
                String brand = brandEditText.getText().toString();
                String tagsInput = tagsEditText.getText().toString();
                String[] tags = tagsInput.split("[ \n]+");



                clothingItem.image = imageUri;
                clothingItem.type = type;
                clothingItem.colour = colour;
                clothingItem.brand = brand;
                clothingItem.tags = Arrays.asList(tags);

                clothingDbViewModel.insertClothingItem(clothingItem);
                navigateToClothingFragment();
            }
        });


        return view;
    }

    private void navigateToClothingFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_edit_clothing_to_navigation_clothing);
    }

    private ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri result) {
                            if (result != null) {
                                // Set the selected image to the ImageView
                                imageView.setImageURI(result);
                            }
                        }
                    });

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
