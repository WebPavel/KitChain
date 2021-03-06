package com.kangsting.kitchain;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomUtils;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class KitController {
    @FXML
    private TextField inputField;
    @FXML
    private TextField optionField;
    @FXML
    private Button uploadBtn;
    @FXML
    private TextArea contentTextArea;
    @FXML
    private TextArea resultTextArea;

    @FXML
    private Button resetBtn;

    @FXML
    private Button base64EncodeBtn;
    @FXML
    private Button base64DecodeBtn;
    @FXML
    private Button img2Base64Btn;
    @FXML
    private Button md5EncodeBtn;
    @FXML
    private Button rsaPubKeyEncodeBtn;
    @FXML
    private Button urlEncodeBtn;
    @FXML
    private Button urlDecodeBtn;
    @FXML
    private Button date2timestampBtn;
    @FXML
    private Button timestamp2DateBtn;
    @FXML
    private Button qrDecodeBtn;
    @FXML
    private Button qrEncodeBtn;
    @FXML
    private Button pingBtn;
    @FXML
    private Button telnetBtn;
    @FXML
    private Button pathBtn;
    @FXML
    private Button openBrowserBtn;
    @FXML
    private Button randomStringGeneratorBtn;

    private Stage primaryStage;
    private File uploadFile;
    private File outputFile;
    private File outputOptionFile;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    protected void onUploadAction() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("???????????????");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG??????", "*.png"),
                    new FileChooser.ExtensionFilter("JPG??????", "*.jpg"),
                    new FileChooser.ExtensionFilter("JPEG??????", "*.jpeg")
            );
            uploadFile = fileChooser.showOpenDialog(primaryStage);
            if (uploadFile == null || !uploadFile.exists() || !uploadFile.isFile()) {
                resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????????????????????????????");
                return;
            }
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????" + uploadFile.getName());
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onClearAction() {
        inputField.setText("");
        optionField.setText("");
        contentTextArea.setText("");
        resultTextArea.setText("");
    }

    @FXML
    protected void onBase64EncodeAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            String encode = new String(Base64.getEncoder().encode(text.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Base64???????????????" + encode);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Base64???????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onBase64DecodeAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            String decode = new String(Base64.getDecoder().decode(text.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Base64???????????????" + decode);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Base64???????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onImg2Base64Action() {
        if (uploadFile == null || !uploadFile.exists() || !uploadFile.isFile()) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "??????????????????");
            return;
        }
        new Thread(() -> {
            try {
                byte[] byteArray = FileUtils.readFileToByteArray(uploadFile);
                String encode = new String(Base64.getEncoder().encode(byteArray), StandardCharsets.UTF_8);
                Platform.runLater(() -> resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????Base64?????????\n" + encode));
            } catch (Exception e) {
                Platform.runLater(() -> resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????Base64?????????" + e.getMessage()));
            }
        }).start();
    }

    @FXML
    protected void onMd5EncodeAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            String md5Hex = DigestUtils.md5Hex(text);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "MD5???????????????" + md5Hex);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "MD5???????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onRsaPubKeyEncodeAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        String content = contentTextArea.getText();
        if (content == null || "".equals(content.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "??????????????????");
            return;
        }
        try {
            byte[] decode = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decode));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String encode = new String(Base64.getEncoder().encode(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "RSA?????????????????????" + encode);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "RSA?????????????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onUrlEncodeAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            String encode = URLEncoder.encode(text, StandardCharsets.UTF_8.name());
            resultTextArea.setText(resultTextArea.getText() + "\n" + "URL???????????????" + encode);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "URL???????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onUrlDecodeAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            String decode = URLDecoder.decode(text, StandardCharsets.UTF_8.name());
            resultTextArea.setText(resultTextArea.getText() + "\n" + "URL???????????????" + decode);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "URL???????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onDate2timestampAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????????????????(??????????????????)" + Instant.now().toEpochMilli());
            return;
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            long epochMilli = localDateTime.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????????????????" + epochMilli);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onTimestamp2DateAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            resultTextArea.setText(resultTextArea.getText() + "\n" + "???????????????????????????(???????????????)" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
            return;
        }
        try {
            long timestamp = Long.parseUnsignedLong(text);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            String dateTimeFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "???????????????????????????" + dateTimeFormatted);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "???????????????????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onQrDecodeAction() {
        if (uploadFile == null || !uploadFile.exists() || !uploadFile.isFile()) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "??????????????????");
            return;
        }
        new Thread(() -> {
            try {
                BufferedImage bufferedImage = ImageIO.read(uploadFile);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
                Map<DecodeHintType, Object> hints = new HashMap<>();
                hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
                Result result = new MultiFormatReader().decode(binaryBitmap, hints);
                bufferedImage.flush();
                Platform.runLater(() -> resultTextArea.setText(resultTextArea.getText() + "\n" + "????????????????????????" + result.getText()));
            } catch (Exception e) {
                Platform.runLater(() -> resultTextArea.setText(resultTextArea.getText() + "\n" + "????????????????????????" + e.getMessage()));
            }
        }).start();
    }

    @FXML
    protected void onQrEncodeAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            String outDir = System.getProperty("user.home");
            if (uploadFile != null && uploadFile.exists() && uploadFile.isFile()) {
                outDir = uploadFile.getParent();
            }
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200, hints);
            BufferedImage matrixImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            String formatName = "gif";
            outputFile = new File(outDir, Long.toHexString(System.currentTimeMillis()) + FilenameUtils.EXTENSION_SEPARATOR + formatName);
            ImageIO.write(matrixImage, formatName, outputFile);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "????????????????????????" + outputFile.getAbsolutePath());
            if (uploadFile != null && uploadFile.exists() && uploadFile.isFile()) {
                Graphics2D graphics = matrixImage.createGraphics();
                int width = matrixImage.getWidth();
                int height = matrixImage.getHeight();
                BufferedImage uploadImage = ImageIO.read(uploadFile);
                graphics.drawImage(uploadImage, width / 5 * 2, height / 5 * 2, width / 5, height / 5, null);
                // ??????????????????
                graphics.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.setColor(Color.white);
                // ???????????????????????????
                graphics.draw(new RoundRectangle2D.Float(width / 5 * 2, height / 5 * 2, width / 5, height / 5, 20, 20));
                // ??????Logo????????????
                graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.setColor(Color.gray);
                graphics.draw(new RoundRectangle2D.Float(width / 5 * 2 + 2, height / 5 * 2 + 2, width / 5 - 4, height / 5 - 4, 20, 20));
                graphics.dispose();
                matrixImage.flush();
                outputOptionFile = new File(outDir, "Logo_" + Long.toHexString(System.currentTimeMillis()) + FilenameUtils.EXTENSION_SEPARATOR + formatName);
                ImageIO.write(matrixImage, formatName, outputOptionFile);
                resultTextArea.setText(resultTextArea.getText() + "\n" + "???????????????+Logo?????????" + outputOptionFile.getAbsolutePath());
            }
            Desktop.getDesktop().open(new File(outDir));
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "????????????????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onPingAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            InetAddress inetAddress = InetAddress.getByName(text);
            boolean reachable = inetAddress.isReachable(5 * 1_000);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Ping???????????????????????????" + reachable);
            if (reachable) {
                resultTextArea.setText(resultTextArea.getText() + "\n" + "Ping???????????????Host name: " + inetAddress.getHostName() + ", IP address: " + inetAddress.getHostAddress());
            }
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Ping???????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onTelnetAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        String option = optionField.getText();
        if (option == null || "".equals(option.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "????????????????????????");
            return;
        }
        try (Socket socket = new Socket()) {
            int port = Integer.parseUnsignedInt(option);
            socket.connect(new InetSocketAddress(text, port), 200);
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Telnet?????????????????????" + socket.isConnected());
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "Telnet?????????" + e.getMessage());
        }
    }

    @FXML
    protected void onPathAction() {
        try {
            Map<String, String> env = System.getenv();
            resultTextArea.setText(resultTextArea.getText() + "\n" + "???????????????????????????" + println(env));
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "???????????????????????????" + e.getMessage());
        }
    }

    private String println(Map<String, String> env) {
        return env.toString().substring(1, env.toString().length() - 1).replace(",", "\t\n");
    }

    @FXML
    protected void onOpenBrowserAction() {
        String text = inputField.getText();
        if (text == null || "".equals(text.trim())) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "?????????????????????");
            return;
        }
        try {
            Desktop.getDesktop().browse(URI.create(text));
            resultTextArea.setText(resultTextArea.getText() + "\n" + "????????????????????????" + text);
        } catch (Exception e) {
            resultTextArea.setText(resultTextArea.getText() + "\n" + "????????????????????????" + e.getMessage());
        }
    }

    @FXML
    protected void onGeneratesRandomStringsAction() {
        String fullText = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+-={}[]|?<>";
        StringBuilder appender = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int idx = RandomUtils.nextInt(0, fullText.length() + 1);
            appender.append(fullText.charAt(idx));
        }
        resultTextArea.setText(resultTextArea.getText() + "\n" + "??????????????????????????????" + appender);
    }
}