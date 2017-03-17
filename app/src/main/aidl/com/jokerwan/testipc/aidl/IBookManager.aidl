package com.jokerwan.testipc.aidl;

import com.jokerwan.testipc.aidl.Book;
import com.jokerwan.testipc.aidl.IOnNewBookArrivedListener;

interface IBookManager {
     List<Book> getBookList();
     void addBook(in Book book);
     void registerListener(IOnNewBookArrivedListener listener);
     void unregisterListener(IOnNewBookArrivedListener listener);
}