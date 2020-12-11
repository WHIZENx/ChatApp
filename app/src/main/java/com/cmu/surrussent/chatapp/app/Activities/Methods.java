package com.cmu.surrussent.chatapp.app.Activities;

import com.cmu.surrussent.chatapp.R;

/**
 * Created by delaroy on 3/21/17.
 */
public class Methods {

    public void setColorTheme(){

        switch (Constant.color){
            case 0xffF44336:
                Constant.theme = R.style.AppTheme_red;
                Constant.theme_home = R.style.AppTheme_red_home;
                break;
            case 0xffE91E63:
                Constant.theme = R.style.AppTheme_pink;
                Constant.theme_home = R.style.AppTheme_pink_home;
                break;
            case 0xff9C27B0:
                Constant.theme = R.style.AppTheme_darkpink;
                Constant.theme_home = R.style.AppTheme_darkpink_home;
                break;
            case 0xff673AB7:
                Constant.theme = R.style.AppTheme_violet;
                Constant.theme_home = R.style.AppTheme_violet_home;
                break;
            case 0xff3F51B5:
                Constant.theme = R.style.AppTheme_blue;
                Constant.theme_home = R.style.AppTheme_blue_home;
                break;
            case 0xff2196f3:
                Constant.theme = R.style.AppTheme_lightblue;
                Constant.theme_home = R.style.AppTheme_lightblue_home;
                break;
            case 0xff03A9F4:
                Constant.theme = R.style.AppTheme_skyblue;
                Constant.theme_home = R.style.AppTheme_skyblue_home;
                break;
            case 0xff00BCD4:
                Constant.theme = R.style.AppTheme_lightskyblue;
                Constant.theme_home = R.style.AppTheme_lightskyblue_home;
                break;
            case 0xff009688:
                Constant.theme = R.style.AppTheme_blackgreen;
                Constant.theme_home = R.style.AppTheme_blackgreen_home;
                break;
            case 0xff4CAF50:
                Constant.theme = R.style.AppTheme_green;
                Constant.theme_home = R.style.AppTheme_green_home;
                break;
            case 0xff8BC34A:
                Constant.theme = R.style.AppTheme_lightgreen;
                Constant.theme_home = R.style.AppTheme_lightgreen_home;
                break;
            case 0xffCDDC39:
                Constant.theme = R.style.AppTheme_skygreen;
                Constant.theme_home = R.style.AppTheme_skygreen_home;
                break;
            case 0xffFFEB3B:
                Constant.theme = R.style.AppTheme_yellow;
                Constant.theme_home = R.style.AppTheme_yellow_home;
                break;
            case 0xffFFC107:
                Constant.theme = R.style.AppTheme_lightorange;
                Constant.theme_home = R.style.AppTheme_lightorange_home;
                break;
            case 0xffFF5722:
                Constant.theme = R.style.AppTheme_darkorange;
                Constant.theme_home = R.style.AppTheme_darkorange_home;
                break;
            case 0xffFF9800:
                Constant.theme = R.style.AppTheme_orange;
                Constant.theme_home = R.style.AppTheme_orange_home;
                break;
            case 0xff9E9E9E:
                Constant.theme = R.style.AppTheme_grey;
                Constant.theme_home = R.style.AppTheme_grey_home;
                break;
            case 0xff795548:
                Constant.theme = R.style.AppTheme_brown;
                Constant.theme_home = R.style.AppTheme_brown_home;
                break;
            case 0xff607D8B:
                Constant.theme = R.style.AppTheme_darkgrey;
                Constant.theme_home = R.style.AppTheme_darkgrey_home;
                break;
            case 0xff000000:
                Constant.theme = R.style.AppTheme_black;
                Constant.theme_home = R.style.AppTheme_black_home;
                break;
            case 0xffFFFFFF:
                Constant.theme = R.style.AppTheme_white;
                Constant.theme_home = R.style.AppTheme_white_home;
                break;
            default:
                break;
        }
    }
}
