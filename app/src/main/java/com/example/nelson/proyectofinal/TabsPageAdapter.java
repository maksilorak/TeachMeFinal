package com.example.nelson.proyectofinal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TabsPageAdapter extends FragmentPagerAdapter{

    public TabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                UsersFragment usersFragment =new UsersFragment();
                return usersFragment;


            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;


            case 2:
                FriendsFragment friendsFragment =  new FriendsFragment();
                return friendsFragment;

            case 3:
                RequestsFragment requestsFragment =  new RequestsFragment();
                return requestsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }


    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Users";

            case 1:
                return "Chats";

            case 2:
                return "Friends";

            case 3:
                return "Requests";

            default:
                return null;
        }
    }
}
