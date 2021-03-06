package hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.util;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.CaffJson;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.Ciff;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.Credit;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.exception.CbNativeParserException;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.model.Image;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.service.GifSequenceWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.service.PpmReader.ppm;

@Slf4j
public class NativeParserUtil {

    private static final String UUID_PARAMETER = "{uuid}";

    private static final String GIF_PATH = "/{uuid}.gif";
    private static final String CAFF_PATH = "/{uuid}.caff";
    private static final String JSON_PATH = "/{uuid}.json";

    private static final String SERVER_IMAGES_PATH = "/caff-browser-backend/src/main/resources/static";
    private static final String PARSER_EXE = "/caff-browser-backend/src/main/resources/caff-browser-native-parser.exe";
    private static final String PARSER_OUTPUT_JSON_PATH = "/caff-browser-native-parser/output-json";
    private static final String PARSER_OUTPUT_IMAGES_PATH = "/caff-browser-native-parser/output-images";

    private static final String REPOSITORY_PATH = getRepositoryPath();

    private NativeParserUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getGifPath(String uuid) {
        return GIF_PATH.replace(UUID_PARAMETER, uuid);
    }

    public static String getCaffPath(String uuid) {
        return CAFF_PATH.replace(UUID_PARAMETER, uuid);
    }

    private static String getJsonPath(String uuid) {
        return JSON_PATH.replace(UUID_PARAMETER, uuid);
    }

    private static String getRepositoryPath() {
        return FileSystems.getDefault().getPath("").normalize().toAbsolutePath().toString()
                .replace("\\", "/")
                .replace("/caff-browser-backend", "");
    }

    /**
     * A fogadott CIFF vagy CAFF f??jlt GIF f??jlk??nt, illetve kieg??sz??t?? JSON adatf??jlk??nt
     * menti a f??jlrendszerre egy helyben gener??lt UUID alapj??n, melyet tov??bb??t az alkalmaz??snak
     *
     * @param file felt??lt??tt CIFF vagy CAFF f??jl
     * @return gener??lt UUID azonos??t??
     */
    public static Image parse(MultipartFile file) throws IOException, InterruptedException {
        log.trace("NativeParserUtil : parse, file=[{}]", file);

        validateFormat(file);
        initDirectories();

        String uuid = saveCaff(file);

        String exe = getRepositoryPath() + PARSER_EXE;
        String path = getRepositoryPath();

        callParser(exe, path, uuid);
        CaffJson caffJson = parseJson(uuid);
        createGif(caffJson, uuid);

        return createImageModel(uuid, caffJson);
    }

    private static void initDirectories() {
        File outputImagesDir = getDirectory();
        File outputJsonDir = new File(REPOSITORY_PATH + PARSER_OUTPUT_JSON_PATH);
        if (!outputImagesDir.exists()) {
            outputImagesDir.mkdirs();
        }
        if (!outputJsonDir.exists()) {
            outputJsonDir.mkdirs();
        }
    }

    private static void callParser(String exe, String path, String uuid) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(exe, path, uuid);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = processBuilder.start();
        process.waitFor();
    }

    private static void validateFormat(MultipartFile file) {
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".caff")) {
            throw new CbNativeParserException("The image is not a .caff file");
        }
    }

    private static String saveCaff(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        file.transferTo(new File(REPOSITORY_PATH + SERVER_IMAGES_PATH + getCaffPath(uuid)));
        return uuid;
    }

    private static CaffJson parseJson(String uuid) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(REPOSITORY_PATH + PARSER_OUTPUT_JSON_PATH + getJsonPath(uuid))));
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CaffJson> jsonAdapter = moshi.adapter(CaffJson.class);
        return jsonAdapter.fromJson(content);
    }

    private static void createGif(CaffJson caffJson, String uuid) throws IOException {
        ImageOutputStream output = new FileImageOutputStream(new File(REPOSITORY_PATH + SERVER_IMAGES_PATH + getGifPath(uuid)));
        GifSequenceWriter writer = new GifSequenceWriter(output, 1, Math.toIntExact(caffJson.getAnimation().getDurations().get(0)), true);

        int width = (int) getFirstElement(caffJson).getWidth();
        int height = (int) getFirstElement(caffJson).getHeight();
        for (File image : getGifParts(uuid)) {
            byte[] fileContent = Files.readAllBytes(image.toPath());
            BufferedImage bufferedImage = ppm(width, height, Arrays.copyOfRange(fileContent, fileContent.length - width * height * 3, fileContent.length));
            writer.writeToSequence(bufferedImage);
        }

        writer.close();
        output.close();
    }

    private static Ciff getFirstElement(CaffJson caff) {
        return caff.getAnimation().getCiffs().get(0);
    }

    private static List<File> getGifParts(String uuid) {
        return getFilesInDirectory()
                .filter(file -> file.getName().startsWith(uuid))
                .collect(Collectors.toList());
    }

    private static Stream<File> getFilesInDirectory() {
        return Arrays.stream(Objects.requireNonNull(getDirectory().listFiles()));
    }

    private static File getDirectory() {
        return new File(REPOSITORY_PATH + PARSER_OUTPUT_IMAGES_PATH);
    }

    private static Image createImageModel(String uuid, CaffJson caff) {
        Credit credit = caff.getCredit();

        Image image = new Image();
        image.setUuid(uuid);
        image.setDate(getLocalDateTime(credit));
        image.setCredit(credit.getCreator());
        image.setCaption(getCaption(caff));
        image.setTags(getTags(caff));
        image.setHeight(getFirstElement(caff).getHeight());
        image.setWidth(getFirstElement(caff).getWidth());
        return image;
    }

    private static LocalDateTime getLocalDateTime(Credit credit) {
        return LocalDateTime.of((int) credit.getYear(), (int) credit.getMonth(), (int) credit.getDay(),
                (int) credit.getHour(), (int) credit.getMinute());
    }

    private static String getCaption(CaffJson caff) {
        return caff.getAnimation().getCiffs()
                .stream()
                .map(Ciff::getCaption)
                .findFirst()
                .orElse(null);
    }

    private static Set<String> getTags(CaffJson caff) {
        return caff.getAnimation().getCiffs()
                .stream()
                .map(Ciff::getTags)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
