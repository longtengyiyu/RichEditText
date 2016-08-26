package com.longtengyiyu.et.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.longtengyiyu.et.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Author:    Oscar
 * Version    V1.0
 * Date:      2016/8/26
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2016/8/26        Oscar           1.0                    1.0
 * Why & What is modified:
 */
public class RichEditText extends EditText {

    //话题正则表达式
    private static final String TOPIC_REGEX = "#([^#]+?)#";

    //话题#符号
    private static final String TAG_REGEX = "#";

    //话题列表
    private ArrayList<String> mTopicList = new ArrayList<>();

    //话题高亮的span列表
    private ArrayList<ForegroundColorSpan> mColorSpans = new ArrayList<>();

    public RichEditText(Context context) {
        this(context, null);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews(){
        requestFocus();
        setFocusable(true);
        setFocusableInTouchMode(true);
        addTextChangedListener(textWatcher);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL){
            int selectionStart = getSelectionStart(); //光标起始位置
            int selectionEnd = getSelectionEnd(); //光标终止位置
            if (selectionStart != selectionEnd){ //光标未选择文本
                return false;
            }
            String content = getText().toString(); //获取文本内容
            int lastPos = 0;
            int size = mTopicList.size();
            for (int i = 0; i < size; i++) {
                String topic = mTopicList.get(i);
                lastPos = content.indexOf(topic, lastPos);
                if (lastPos != -1) {
                    if (selectionStart != 0 && selectionStart >= lastPos && selectionStart <= (lastPos + topic.length())) {
                        //直接使用setSelection(start, stop) 而不使用delete、insert方法会导致第二次不会选中的bug
                        getText().delete(lastPos, lastPos + topic.length());
                        getText().insert(lastPos, topic);
//                        replaceTopicImage(); //如果替换了#号
                        setSelection(lastPos, lastPos + topic.length());
//                        Selection.setSelection(getText(), lastPos, lastPos + tag.length());
                        return true;
                    }
                }
                lastPos += topic.length();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) return;
            String content = s.toString();
            mTopicList.clear();
            mTopicList.addAll(findTopic(s.toString()));

            for (int i = 0; i < mColorSpans.size(); i++) {
                s.removeSpan(mColorSpans.get(i));
            }
            mColorSpans.clear();
            int topicPosition = 0;
            int size = mTopicList.size();
            for (int i = 0; i < size; i++) {
                String topic = mTopicList.get(i);
                topicPosition = content.indexOf(topic, topicPosition);
                if (topicPosition != -1) {
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    s.setSpan(colorSpan, topicPosition, topicPosition = topicPosition + topic.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //设置话题高亮
                    mColorSpans.add(colorSpan);
                }
            }
        }
    };

    /**
     * 查找话题
     * @param content 文本内容
     */
    private static ArrayList<String> findTopic(String content){
        Pattern p = Pattern.compile(TOPIC_REGEX);
        Matcher m = p.matcher(content);
        ArrayList<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    /**
     * 替换话题标签符号为图片
     */
    public void replaceTopicImage(){
        if (TextUtils.isEmpty(getText())){
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(getText());
        Pattern pattern = Pattern.compile(TAG_REGEX);
        Matcher matcher = pattern.matcher(getText());

        while (matcher.find()) {
            builder.setSpan(new ImageSpan(getContext(), R.drawable.ic_topic), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(builder);
        setSelection(getText().toString().length());
    }

    /**
     * 替换话题标签符号为图片
     *
     * @param res 图片资源文件
     */
    public void replaceTopicImage(int res) {
        if (TextUtils.isEmpty(getText())){
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(getText());
        Pattern pattern = Pattern.compile(TAG_REGEX);
        Matcher matcher = pattern.matcher(getText());

        while (matcher.find()) {
            builder.setSpan(new ImageSpan(getContext(), res), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(builder);
        setSelection(getText().toString().length());
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelectionPosition();
    }

    /**
     * 点击话题设置光标落入结束的话题#之后
     */
    private void setSelectionPosition(){
        if (TextUtils.isEmpty(getText().toString().trim())){
            return;
        }
        if (mTopicList == null || mTopicList.size() == 0){
            return;
        }
        int selectionStart = getSelectionStart();
        int lastPos = 0;
        int size = mTopicList.size();
        for (int i = 0; i < size; i++) {
            String tag = mTopicList.get(i);
            lastPos = getText().toString().indexOf(tag, lastPos);

            if (lastPos != -1) {
                if (selectionStart > lastPos && selectionStart <= (lastPos + tag.length())) {
                    //还可以根据点击话题的位置将光标移到话题开始的#前
                    setSelection(lastPos + tag.length());
                }
            }
            lastPos = lastPos + tag.length();
        }
    }
}
