package org.code4everything.wetool.controller;

import cn.hutool.core.util.RandomUtil;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.RandomUtils;
import org.code4everything.wetool.WeUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pantao
 * @since 2018/4/2
 */
public class RandomGeneratorController {

    @FXML
    public TextField uuidResult;

    @FXML
    public TextField floor;

    @FXML
    public TextField ceil;

    @FXML
    public TextField ignoreRange;

    @FXML
    public TextField precision;

    @FXML
    public TextField numberResult;

    @FXML
    public TextField emailResult;

    @FXML
    public TextField lowerCaseLength;

    @FXML
    public TextField lowerCaseResult;

    @FXML
    public TextField upperCaseLength;

    @FXML
    public TextField upperCaseResult;

    @FXML
    public TextField letterLength;

    @FXML
    public TextField letterResult;

    @FXML
    public TextField stringLength;

    @FXML
    public TextField stringResult;

    @FXML
    public TextField textLength;

    @FXML
    public TextField textResult;

    public void generateUUID() {
        uuidResult.setText(RandomUtil.randomUUID());
    }

    public void generateNumber() {
        String flo = floor.getText();
        String cei = ceil.getText();
        int p = WeUtils.stringToInt(precision.getText());
        if (p == 0) {
            String[] ranges = Checker.checkNull(ignoreRange.getText()).split(ValueConsts.COMMA_SIGN);
            List<int[]> rl = new ArrayList<>(ranges.length);
            for (String range : ranges) {
                String[] rs = range.trim().split("-");
                if (rs.length > 1 && Checker.isDecimal(rs[0].trim()) && Checker.isDecimal(rs[1].trim())) {
                    int[] ri = new int[]{Formatter.stringToInt(rs[0]), Formatter.stringToInt(rs[1])};
                    rl.add(ri);
                }
            }
            int[][] r = new int[rl.size()][2];
            rl.toArray(r);
            int f = WeUtils.stringToInt(flo);
            int c = Checker.isDecimal(cei) ? Formatter.stringToInt(cei) : Integer.MAX_VALUE;
            numberResult.setText(String.valueOf(RandomUtils.getRandomIntegerIgnoreRange(f, c, r)));
        } else {
            double f = WeUtils.stringToDouble(flo);
            double c = Checker.isDecimal(cei) ? Formatter.stringToDouble(cei) : Double.MAX_VALUE;
            numberResult.setText(String.valueOf(RandomUtils.getRandomDouble(f, c, p)));
        }
    }

    public void generateEmail() {
        emailResult.setText(RandomUtils.getRandomEmail());
    }

    public void generateLowerCase() {
        lowerCaseResult.setText(RandomUtils.getRandomStringOnlyLowerCase(getLength(lowerCaseLength.getText())));
    }

    public void generateUpperCase() {
        upperCaseResult.setText(RandomUtils.getRandomStringOnlyUpperCase(getLength(upperCaseLength.getText())));
    }

    public void generateLetter() {
        letterResult.setText(RandomUtils.getRandomStringOnlyLetter(getLength(letterLength.getText())));
    }

    public void generateString() {
        stringResult.setText(RandomUtils.getRandomStringWithoutSymbol(getLength(stringLength.getText())));
    }

    public void generateText() {
        textResult.setText(RandomUtils.getRandomString(getLength(textLength.getText())));
    }

    private int getLength(String len) {
        int length = WeUtils.stringToInt(len);
        return length < 1 ? RandomUtils.getRandomInteger(ValueConsts.NINE_INT, ValueConsts.SIXTEEN_INT) : length;
    }
}
