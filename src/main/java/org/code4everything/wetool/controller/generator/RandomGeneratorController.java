package org.code4everything.wetool.controller.generator;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.util.FinalUtils;

/**
 * @author pantao
 * @since 2018/4/2
 */
@Slf4j
public class RandomGeneratorController implements BaseViewController {

    private final String BASE_NUMBER = "0123456789";

    private final String BASE_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final String BASE_LOWER = "abcdefghijklmnopqrstuvwxyz";

    private final String BASE_LETTER = BASE_LOWER + BASE_UPPER;

    private final String BASE_STRING = BASE_NUMBER + BASE_LETTER;

    private final String BASE_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private final String BASE_TEXT = BASE_CHAR + BASE_STRING;

    @FXML
    public TextField uuidResult;

    @FXML
    public TextField floor;

    @FXML
    public TextField ceil;

    @FXML
    public TextField precision;

    @FXML
    public TextField numberResult;

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

    @FXML
    private void initialize() {
        log.info("open tab for random generator");
        FinalUtils.registerView(TitleConsts.RANDOM_GENERATOR, this);
    }

    public void generateUUID() {
        uuidResult.setText(IdUtil.simpleUUID());
        log.info("generate random uuid: {}", uuidResult.getText());
    }

    public void generateNumber() {
        int low = WeUtils.parseInt(floor.getText(), Integer.MIN_VALUE);
        int high = WeUtils.parseInt(ceil.getText(), low + 1);
        int scale = WeUtils.parseInt(precision.getText(), 0);
        String num = String.valueOf(RandomUtil.randomInt(low, high));
        if (scale > 0) {
            low = 0;
            high = 10;
            if (scale > 1) {
                low = (int) Math.pow(10, scale - 1);
                high = low * 10;
            }
            num += "." + RandomUtil.randomInt(low + 1, high);
        }
        numberResult.setText(num);
        log.info("generate random number: {}", numberResult.getText());
    }

    public void generateLowerCase() {
        lowerCaseResult.setText(RandomUtil.randomString(BASE_LOWER, parseInt(lowerCaseLength.getText())));
        log.info("generate random lower case: {}", lowerCaseResult.getText());
    }

    public void generateUpperCase() {
        upperCaseResult.setText(RandomUtil.randomString(BASE_UPPER, parseInt(upperCaseLength.getText())));
        log.info("generate random upper case: {}", upperCaseResult.getText());
    }

    public void generateLetter() {
        letterResult.setText(RandomUtil.randomString(BASE_LETTER, parseInt(letterLength.getText())));
        log.info("generate random letter: {}", letterResult.getText());
    }

    public void generateString() {
        stringResult.setText(RandomUtil.randomString(BASE_STRING, parseInt(stringLength.getText())));
        log.info("generate random string: {}", stringResult.getText());
    }

    public void generateText() {
        textResult.setText(RandomUtil.randomString(BASE_TEXT, parseInt(textLength.getText())));
        log.info("generate random text: {}", textResult.getText());
    }

    private int parseInt(String len) {
        return WeUtils.parseInt(len, 1);
    }
}
