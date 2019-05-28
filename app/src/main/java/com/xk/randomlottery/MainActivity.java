package com.xk.randomlottery;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.xk.limitapp.LimitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

public class MainActivity extends AppCompatActivity {

    private FlexboxLayout before;
    private FlexboxLayout after;


    private String[] leftNmuber = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
    //            , "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35"};
    private String[] rightNmuber = {"1", "2", "3", "4", "5"};
    //        , "6", "7", "8", "9", "10", "11", "12", "13"
//            , "14", "15", "16"};
    private NumberPicker leftPicker;
    private NumberPicker rightPicker;

    private int maxSelectBefore = 34;
    private int beforeValue = 1;
    private int maxSelectAfter = 15;
    private int afterValue = 1;
    private TextView log;
    private ScrollView scroll;
    private ClipboardManager myClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LimitUtils.limit(getPackageName(), this);
        setContentView(R.layout.activity_main);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        before = findViewById(R.id.selectBefore);
        after = findViewById(R.id.selectAfter);
        leftPicker = findViewById(R.id.leftPicker);
        rightPicker = findViewById(R.id.rightPicker);


        //设置需要显示的内容数组
        leftPicker.setDisplayedValues(leftNmuber);
        rightPicker.setDisplayedValues(rightNmuber);
        leftPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        rightPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //设置最大最小值
        leftPicker.setMinValue(1);
        leftPicker.setMaxValue(leftNmuber.length);
        rightPicker.setMinValue(1);
        rightPicker.setMaxValue(rightNmuber.length);

        log = findViewById(R.id.log);
        scroll = findViewById(R.id.scroll);
        leftPicker.setValue(1);
        rightPicker.setValue(1);
        leftPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int value = view.getValue();
                    maxSelectBefore = 35 - value;
                    beforeValue = value;
                    for (int i = 0; i < before.getChildCount(); i++) {
                        View childAt = before.getChildAt(i);
                        childAt.setSelected(false);
                        if (beforeValue > afterValue) {
                            childAt.setEnabled(true);
                        } else {
                            childAt.setEnabled(false);
                        }
                    }
                    for (int i = 0; i < after.getChildCount(); i++) {
                        View childAt = after.getChildAt(i);
                        if (beforeValue > afterValue) {
                            childAt.setEnabled(true);
                        } else {
                            childAt.setEnabled(false);
                        }
                    }
                }
            }
        });
        rightPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int value = view.getValue();
                    maxSelectAfter = 16 - value;
                    afterValue = value;
                    for (int i = 0; i < after.getChildCount(); i++) {
                        View childAt = after.getChildAt(i);
                        childAt.setSelected(false);
                        if (beforeValue > afterValue) {
                            childAt.setEnabled(true);
                        } else {
                            childAt.setEnabled(false);
                        }
                    }
                    for (int i = 0; i < before.getChildCount(); i++) {
                        View childAt = before.getChildAt(i);
                        if (beforeValue > afterValue) {
                            childAt.setEnabled(true);
                        } else {
                            childAt.setEnabled(false);
                        }
                    }
                }
            }
        });
        for (int i = 0; i < 35; i++) {
            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setText(format(" %02d ", i + 1));
            textView.setBackground(getDrawable(R.drawable.number_bg));
            before.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected() || checkCanSelect(true)) {
                        v.setSelected(!v.isSelected());
                    } else {
                        Toast.makeText(MainActivity.this, "不能再选了", Toast.LENGTH_SHORT).show();
                    }

                }


            });
        }
        for (int i = 0; i < 16; i++) {
            TextView textView = new TextView(this);
            textView.setText(format(" %02d ", i + 1));
            textView.setTextSize(20);
            textView.setBackground(getDrawable(R.drawable.number_bg));
            after.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected() || checkCanSelect(false)) {
                        v.setSelected(!v.isSelected());
                    } else {
                        Toast.makeText(MainActivity.this, "不能再选了", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean checkCanSelect(boolean isLeft) {
        FlexboxLayout flexboxLayout = isLeft ? before : after;
        int max = isLeft ? maxSelectBefore : maxSelectAfter;

        int selectCount = 0;
        for (int i = 0; i < flexboxLayout.getChildCount(); i++) {
            View childAt = flexboxLayout.getChildAt(i);
            if (childAt.isSelected()) {
                selectCount++;
            }
            if (selectCount >= max) {
                return false;
            }
        }
        return true;
    }

    public void enable(View v) {
        setEnable();
    }

    boolean flag = false;

    public void setEnable() {
        flag = !flag;
        for (int i = 0; i < before.getChildCount(); i++) {
            View childAt = before.getChildAt(i);
            childAt.setEnabled(flag);
        }
        for (int i = 0; i < after.getChildCount(); i++) {
            View childAt = after.getChildAt(i);
            childAt.setEnabled(flag);
        }
    }

    public void copy(View view) {
//        ClipData myClip = ClipData.newPlainText("text", ((TextView) view).getText().toString());
//        myClipboard.setPrimaryClip(myClip);
        myClipboard.setText(((TextView) log).getText().toString());
        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
    }

    public void compute(View view) {
        List<Integer> beforeNumbers = new ArrayList<>();
        List<Integer> beforeResult = new ArrayList<>();
        List<Integer> afterNumbers = new ArrayList<>();
        List<Integer> afterResult = new ArrayList<>();
        for (int i = 0; i < before.getChildCount(); i++) {
            TextView childAt = (TextView) before.getChildAt(i);
            if (!childAt.isSelected()) {
                beforeNumbers.add(Integer.parseInt(childAt.getText().toString().trim()));
            }
        }
        for (int i = 0; i < after.getChildCount(); i++) {
            TextView childAt = (TextView) after.getChildAt(i);
            if (!childAt.isSelected()) {
                afterNumbers.add(Integer.parseInt(childAt.getText().toString().trim()));
            }
        }
        // 初始化随机数
        Random rand = new Random();

        // 遍历整个items数组
        for (int i = 0; i < beforeValue; i++) {
            // 任意取一个0~size的整数，注意此处的items.size()是变化的，所以不能用前面的size会发生数组越界的异常
            int myRand = rand.nextInt(beforeNumbers.size());
            //将取出的这个元素放到存放结果的集合中
            beforeResult.add(beforeNumbers.get(myRand));
            //从原始集合中删除该元素防止重复。注意，items数组大小发生了改变
            beforeNumbers.remove(myRand);
        }

        // 遍历整个items数组
        for (int i = 0; i < afterValue; i++) {
            // 任意取一个0~size的整数，注意此处的items.size()是变化的，所以不能用前面的size会发生数组越界的异常
            int myRand = rand.nextInt(afterNumbers.size());
            //将取出的这个元素放到存放结果的集合中
            afterResult.add(afterNumbers.get(myRand));
            //从原始集合中删除该元素防止重复。注意，items数组大小发生了改变
            afterNumbers.remove(myRand);
        }

        StringBuffer sb = new StringBuffer();
        for (Integer integer : beforeResult) {
            if (integer < 10) {
                sb.append("0");
            }
            sb.append(integer).append(",");
        }
        for (Integer integer : afterResult) {
            if (integer < 10) {
                sb.append("0");
            }
            sb.append(integer).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        String history = log.getText().toString();
        String s = history + "\n" + sb.toString();
        log.setText(s);
        scroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll.scrollTo(0, 10000);
            }
        }, 1000);
    }
}
