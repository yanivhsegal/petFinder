package com.yaniv.petfinder;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.yaniv.petfinder.model.Pet;

public class HomeActivity extends AppCompatActivity implements PetsListFragment.Delegate {
    NavController navCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navCtrl = Navigation.findNavController(this, R.id.home_nav_host);
        NavigationUI.setupActionBarWithNavController(this, navCtrl);
    }

    @Override
    public void onItemSelected(Pet pet) {
//        NavController navCtrl = Navigation.findNavController(this, R.id.home_nav_host);
        NavGraphDirections.ActionGlobalPetDetailsFragment direction = PetsListFragmentDirections.actionGlobalPetDetailsFragment(pet);
        navCtrl.navigate(direction);
    }

    @Override
    public void onSignOut() {
        navCtrl.navigate(R.id.loginFragment);
    }

    @Override
    public void onManageMyPets() {
        NavGraphDirections.ActionGlobalPetsListFragment direction = PetsListFragmentDirections.actionGlobalPetsListFragment(true);
        navCtrl.navigate(direction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.pets_list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navCtrl.navigateUp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
