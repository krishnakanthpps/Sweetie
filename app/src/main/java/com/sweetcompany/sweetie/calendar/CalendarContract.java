package com.sweetcompany.sweetie.calendar;

import com.sweetcompany.sweetie.model.ActionDiaryFB;

import java.util.List;
import java.util.Map;


public interface CalendarContract {

    interface View {
        void setPresenter(Presenter presenter);
        // call after setPresenter()
        void initializeActualMonth();

        void setMonthActionsDiary(Map<String, Map<String, ActionDiaryFB>> monthActionDiary);
    }

    interface Presenter {
        void downloadActionsDiaryForMonth(String yearAndMonth);
    }

}
