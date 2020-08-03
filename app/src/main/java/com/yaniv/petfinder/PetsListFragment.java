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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    interface Delegate {
        void onItemSelected(Pet pet);
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

        FirebaseUser currentUser = mAuth.getCurrentUser();
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


        adapter = new PetsListAdapter();
        list.setAdapter(adapter);

        adapter.setOnIntemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Log.d("TAG", "row was clicked" + position);
                Pet pet = data.get(position);
                parent.onItemSelected(pet);
            }
        });

        liveData = viewModel.getPetsData();
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
        Pet pet;

        public PetRowViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name_tv);
            description = itemView.findViewById(R.id.row_id_tv);
            image = itemView.findViewById(R.id.row_image);
            userImage = itemView.findViewById(R.id.row_user_image);
            userName = itemView.findViewById(R.id.row_user_name);

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
        public void bind(Pet pt) {

            new AsyncTask<String, String, User>() {
                @Override
                protected User doInBackground(String... strings) {
                    User usr = UserModel.instance.getUser(strings[0]);
                    if (usr != null) {
                        userName.setText(usr.name);

                        if (usr.imageUrl != null && usr.imageUrl != "") {
                            Picasso.get().load(usr.imageUrl).placeholder(R.drawable.avatar).into(userImage);
                        } else {
                            userImage.setImageResource(R.drawable.avatar);
                        }
                    }
                    return usr;
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
            Pet st = data.get(i);
            petRowViewHolder.bind(st);

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
                ((HomeActivity) parent).navCtrl.navigate(R.id.loginFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
