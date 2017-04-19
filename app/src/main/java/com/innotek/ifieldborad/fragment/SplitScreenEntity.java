package com.innotek.ifieldborad.fragment;

/**
 * Created by Raleigh.Luo on 17/4/19.
 */

public class SplitScreenEntity {
    private int total=1;//分屏播放，总屏数
    private int currentIndex=1;//当前分屏页
    private int start=0;//开始字符位置
    private int end=0;//结束字符位置

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
