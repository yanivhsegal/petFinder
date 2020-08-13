package com.yaniv.petfinder;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yaniv.petfinder.model.Pet;
import com.yaniv.petfinder.model.PetModel;
import com.squareup.picasso.Picasso;
import com.yaniv.petfinder.model.PetTypes;
import com.yaniv.petfinder.model.User;
import com.yaniv.petfinder.model.UserModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PetsListFragment extends Fragment {
    RecyclerView list;
    List<Pet> petsData = new LinkedList<Pet>();
    List<User> usersData = new LinkedList<User>();
    PetsListAdapter adapter;
    PetListViewModel viewModel;
    LiveData<List<Pet>> livePetsData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageButton searchButton;
    boolean isPetsManagement;
    View view;

    interface Delegate {
        void onItemSelected(Pet pet);

        void onManageMyPets();

        void onSignOut();
    }

    Delegate parent;


    public PetsListFragment() {
//        PetModel.instance.getAllPets(new PetModel.Listener<List<Pet>>() {
//            @Override
//            public void onComplete(List<Pet> _data) {
//                data = _data;
//                if (adapter != null){
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAuth = FirebaseAuth.getInstance();
        try {
            isPetsManagement = PetsListFragmentArgs.fromBundle(getArguments()).getIsPetManagement();
        } catch (Exception e) {
            isPetsManagement = false;
        }
        if (context instanceof Delegate) {
            parent = (Delegate) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + "pet list parent activity must implement pet ;list fragment Delegate");
        }
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(this).get(PetListViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pets_list, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            ((HomeActivity) parent).navCtrl.navigate(PetsListFragmentDirections.actionPetsListFragmentToLoginFragment());
        }

        if (isPetsManagement) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Pets");
        }


//        viewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                if (user.name == null) {
//                    ((HomeActivity) parent).navCtrl.navigate(R.id.loginFragment);
//                }
//            }
//        });

        list = view.findViewById(R.id.pets_list_list);
        list.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);


        adapter = new PetsListAdapter(isPetsManagement);
        list.setAdapter(adapter);

        adapter.setOnIntemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Log.d("TAG", "row was clicked" + position);
                Pet pet = petsData.get(position);
                parent.onItemSelected(pet);
            }
        });

        livePetsData = viewModel.getPetsData(isPetsManagement, currentUser != null ? currentUser.getUid() : "");

        livePetsData.observe(getViewLifecycleOwner(), new Observer<List<Pet>>() {
            @Override
            public void onChanged(List<Pet> pets) {
                petsData = pets;
                adapter.notifyDataSetChanged();
            }
        });

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.pets_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh(new PetModel.CompListener() {
                    @Override
                    public void onComplete() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parent = null;
    }

    static class PetRowViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        ImageView image;
        ImageView userImage;
        TextView userName;
        ImageView delete;
        ImageView edit;
        Pet pet;
        FragmentManager parentFragmentManager;

        public PetRowViewHolder(@NonNull final View itemView, final OnItemClickListener listener, final FragmentManager parentFragmentManager) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name_tv);
            description = itemView.findViewById(R.id.row_description_tv);
            image = itemView.findViewById(R.id.row_image);
            userImage = itemView.findViewById(R.id.row_user_image);
            userName = itemView.findViewById(R.id.row_user_name);
            delete = itemView.findViewById(R.id.row_delete);
            edit = itemView.findViewById(R.id.row_edit);
            this.parentFragmentManager = parentFragmentManager;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onClick(position);
                        }
                    }
                }
            });
        }

        @SuppressLint("StaticFieldLeak")
        public void bind(final Pet pt, Boolean isPetsManagement) {

            if (!isPetsManagement) {
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
            } else {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialogFragment dialog = AlertDialogFragment.newInstance("Delete post", "Are you sure you want to delete post?", pt.id);
                        dialog.show(parentFragmentManager, "TAG");
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NavController navCtrl = Navigation.findNavController(view);
                        NavDirections directions = NewPetFragmentDirections.actionGlobalNewPetFragment(pt);
                        navCtrl.navigate(directions);
                    }
                });
            }

            new AsyncTask<String, String, User>() {
                @Override
                protected User doInBackground(String... strings) {
                    User usr = UserModel.instance.getUser(strings[0]);
                    return usr;
                }

                @Override
                protected void onPostExecute(User user) {
                    super.onPostExecute(user);
                    if (user != null) {
                        userName.setText(user.name);

                        if (user.imageUrl != null && user.imageUrl != "") {
                            Picasso.get().load(user.imageUrl).placeholder(R.drawable.avatar).into(userImage);
                        } else {
                            userImage.setImageResource(R.drawable.avatar);
                        }
                    }
                }
            }.execute(pt.ownerId);


            name.setText(pt.name);
            description.setText(pt.description);
            pet = pt;
            if (pt.imgUrl != null && pt.imgUrl.get(0) != "") {
                Picasso.get().load(pt.imgUrl.get(0)).placeholder(R.drawable.avatar).into(image);
            } else {
                image.setImageResource(R.drawable.avatar);
            }
        }
    }

    interface OnItemClickListener {
        void onClick(int position);
    }

    class PetsListAdapter extends RecyclerView.Adapter<PetRowViewHolder> {
        private OnItemClickListener listener;
        private Boolean isPetsManagement = false;

        public PetsListAdapter() {
        }

        public PetsListAdapter(Boolean isPetsManagement) {
            this.isPetsManagement = isPetsManagement;
        }

        void setOnIntemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }


        @NonNull
        @Override
        public PetRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.list_row, viewGroup, false);
            PetRowViewHolder vh = new PetRowViewHolder(v, listener, getParentFragmentManager());
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PetRowViewHolder petRowViewHolder, int i) {
            Pet pt = petsData.get(i);
            petRowViewHolder.bind(pt, isPetsManagement);
        }

        @Override
        public int getItemCount() {
            return petsData.size();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!this.isPetsManagement) {
            inflater.inflate(R.menu.pets_list_menu, menu);
            searchButton = (ImageButton) menu.findItem(R.id.menu_pet_list_search).getActionView();
            searchButton.setImageResource(R.drawable.search);
            searchButton.setBackground(null);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(getContext(), view);
                    popup.getMenuInflater()
                            .inflate(R.menu.search_by_menu, popup.getMenu());
                    String[] types = Arrays.toString(PetTypes.class.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
                    for (String type : types) {
                        popup.getMenu().add(0, 10000000 + PetTypes.valueOf(type).ordinal(), PetTypes.valueOf(type).ordinal() + 1, type);
                    }
                    popup.getMenu().add(0, 10000000, 0,"Show all types");
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            new AsyncTask<String, String, List<Pet>>() {

                                @Override
                                protected List<Pet> doInBackground(String... strings) {
                                    try{
                                        PetTypes petType = PetTypes.valueOf(strings[0]);
                                        petsData = PetModel.instance.getAllPetsByType(PetTypes.valueOf(strings[0]));
                                    }catch(Exception e){
                                        viewModel.refresh(new PetModel.CompListener() {
                                            @Override
                                            public void onComplete() {
                                                return;
                                            }
                                        });
                                    }
                                    return petsData;
                                }

                                @Override
                                protected void onPostExecute(List<Pet> pets) {
                                    super.onPostExecute(pets);
                                    adapter.notifyDataSetChanged();
                                }
                            }.execute(menuItem.getTitle().toString());
                            return true;
                        }
                    });
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navCtrl;
        switch (item.getItemId()) {
            case R.id.menu_pet_list_add:
                Log.d("TAG", "fragment handle add menu");
                navCtrl = Navigation.findNavController(list);
                NavDirections directions = NewPetFragmentDirections.actionGlobalNewPetFragment(null);
                navCtrl.navigate(directions);
                return true;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                parent.onSignOut();
                return true;
            case R.id.manage_pets:
                parent.onManageMyPets();
                return true;
            case R.id.menu_pet_list_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
