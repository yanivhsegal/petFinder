package com.yaniv.petfinder;


import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import com.yaniv.petfinder.model.User;
import com.yaniv.petfinder.model.UserModel;

import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PetsListFragment extends Fragment {
    RecyclerView list;
    List<Pet> data = new LinkedList<Pet>();
    PetsListAdapter adapter;
    PetListViewModel viewModel;
    LiveData<List<Pet>> liveData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    boolean isPetsManagement;

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
                    + "pet list parent activity must implement dtudent ;list fragment Delegate");
        }
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(this).get(PetListViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pets_list, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            ((HomeActivity) parent).navCtrl.navigate(R.id.loginFragment);
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
                Pet pet = data.get(position);
                parent.onItemSelected(pet);
            }
        });

        liveData = viewModel.getPetsData(isPetsManagement, currentUser != null ? currentUser.getUid() : "");

        liveData.observe(getViewLifecycleOwner(), new Observer<List<Pet>>() {
            @Override
            public void onChanged(List<Pet> pets) {
                data = pets;
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
        Pet pet;

        public PetRowViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name_tv);
            description = itemView.findViewById(R.id.row_description_tv);
            image = itemView.findViewById(R.id.row_image);
            userImage = itemView.findViewById(R.id.row_user_image);
            userName = itemView.findViewById(R.id.row_user_name);
            delete  = itemView.findViewById(R.id.row_delete);

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
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @SuppressLint("StaticFieldLeak")
        public void bind(Pet pt, Boolean isPetsManagement) {

            if(!isPetsManagement){
                delete.setVisibility(View.GONE);
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
            if (pt.imgUrl != null && pt.imgUrl != "") {
                Picasso.get().load(pt.imgUrl).placeholder(R.drawable.avatar).into(image);
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
            PetRowViewHolder vh = new PetRowViewHolder(v, listener);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PetRowViewHolder petRowViewHolder, int i) {
            Pet pt = data.get(i);
            petRowViewHolder.bind(pt, isPetsManagement);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pets_list_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pet_list_add:
                Log.d("TAG", "fragment handle add menu");
                NavController navCtrl = Navigation.findNavController(list);
                NavDirections directions = NewPetFragmentDirections.actionGlobalNewPetFragment();
                navCtrl.navigate(directions);
                return true;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                parent.onSignOut();
                return true;
            case R.id.manage_pets:
                parent.onManageMyPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
