package com.example.groupproject.ui.relations;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {
    /**
     * Live data container for tabbed list.
     */

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "Once integrated, this page will display: " + input;
        }
    });

    public void setIndex(int index) {
        /**
         * Initialize value to live tab index value
         */
        mIndex.setValue(index);
    }

    public int getIndex(){
        /**
         * Returns the value of index
         * @return index Current index value
         */
        return mIndex.getValue();
    }

    public LiveData<String> getText() {
        return mText;
    }
}