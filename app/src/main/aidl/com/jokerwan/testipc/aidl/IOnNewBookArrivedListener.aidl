package com.jokerwan.testipc.aidl;

import com.jokerwan.testipc.aidl.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
