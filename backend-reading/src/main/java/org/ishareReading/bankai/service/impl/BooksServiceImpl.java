package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.ishare.oss.ObjectNameUtil;
import org.ishare.oss.OssService;
import org.ishareReading.bankai.constant.BucketConstant;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.mapper.BookContentPageMapper;
import org.ishareReading.bankai.mapper.BooksMapper;
import org.ishareReading.bankai.model.*;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BooksService;
import org.ishareReading.bankai.service.FilesService;
import org.ishareReading.bankai.utils.BookUtils;
import org.ishareReading.bankai.utils.FileUtil;
import org.ishareReading.bankai.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * 书籍表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private OssService ossService;
    @Autowired
    private FilesService filesService;
    @Autowired
    private BooksService booksService;
    @Autowired
    private OssService osService;
    @Autowired
    private BookContentPageMapper bookContentPageMapper;

    @Autowired
    private BookUtils bookUtils;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SqlSession sqlSession;

    public static void main(String[] args) throws Exception {
        File file = new File("./out.pdf");
        try (PDDocument pdDocument = Loader.loadPDF(new FileInputStream(file).readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(5);
            stripper.setEndPage(5);
            String text = stripper.getText(pdDocument);
            System.out.println(text);
        }
    }


    @Override
    public List<Books> getRecentlyReleaseBook() {
        return list(new LambdaQueryWrapper<Books>().orderByDesc(Books::getUploadTime).last("limit 5"));
    }

    @Override
    public List<HotBook> getBookHotRank() {
        final Set<ZSetOperations.TypedTuple<Object>> zSet = redisTemplate.opsForZSet().reverseRangeWithScores(RedisConstant.HOT_BOOK, 0, -1);
        final ArrayList<HotBook> hotBooks = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> objectTypedTuple : zSet) {
            final HotBook hotBook;
            try {
                hotBook = objectMapper.readValue(objectTypedTuple.getValue().toString(), HotBook.class);
                hotBook.setHot((double) objectTypedTuple.getScore().intValue());
                hotBook.hotFormat();
                hotBooks.add(hotBook);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return hotBooks;
    }

    /**
     * 上传图书 1.保存到阿里云 2.书籍页数表 ，读取段落？？？ 一页 ？？？ 3.文件预览？？？直接使用kkfileview吧，拉下来搞个包
     *
     * @param books
     *
     * @return
     */
    @SneakyThrows
    @Override
    public Response uploadOrUpdateBook(Books books) {
        boolean b = booksService.save(books);
        new Thread(() -> {
//            ByteArrayInputStream pdfContent = new ByteArrayInputStream(bytes);
            //TODO 异步进行数据内容处理？？、
            if (b) {
                booksService.insertContestPages(books.getFileId(), books.getTotalPages());
            }
        }).start();
        return Response.success("添加成功");
    }

    /**
     * pdfbox 读取pdf获取元数据？？？ 且上传
     *
     * @return
     */
    @SneakyThrows
    @Override
    public Response getMetadata(MultipartFile multipartFile) {
//        这里进行文件转码处理都转成PDF/MD
        String contentType = multipartFile.getContentType();
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = FileUtil.getExtension(originalFilename);
        String dbType = FileUtil.detectFileType(multipartFile); //数据库对应的中文类型
        InputStream inputStream = multipartFile.getInputStream();
        byte[] bytes = inputStream.readAllBytes();
        String coverageId = convertFirstPageToImage(new ByteArrayInputStream(bytes));
        String bookPdfId = ObjectNameUtil.chunkSha256(inputStream);
        if (!ossService.doesObjectExist(BucketConstant.COMMON_BUCKET_NAME, bookPdfId)) {
            bookPdfId = ossService.upload(BucketConstant.COMMON_BUCKET_NAME, new ByteArrayInputStream(bytes), originalFilename);
        }
//        对直接流进行异步处理，存储每一页的内容和  todo 字节流？？？ 这个不太行啊，删掉吧 ， 存储内容就行了
        Files bookFile = new Files();
        bookFile.setSize(multipartFile.getSize());
        bookFile.setType(dbType);
        bookFile.setFormat(contentType);
        bookFile.setFileName(originalFilename);
        bookFile.setFilePath(bookPdfId);
        bookFile.setExtension(extension);
        bookFile.setUserId(UserHolder.get());
        filesService.save(bookFile);


        Files imgFile = new Files();
        imgFile.setFormat("image/jpeg");
        imgFile.setType("图片");
        imgFile.setFileName(Objects.requireNonNull(originalFilename).substring(0, originalFilename.lastIndexOf(".") + 1) + ".jpg");
        imgFile.setFilePath(coverageId);
        imgFile.setExtension("jpg");
        imgFile.setUserId(UserHolder.get());
        filesService.save(imgFile);
        new ByteArrayInputStream(bytes);
//        cover img id
//        book id

        Object metadata = bookUtils.getMetadata(bytes);
        record TMP(BookUtils.MetaData metaData, String imgId, String bookId) {
        }
        return Response.success(
                new TMP((BookUtils.MetaData) metadata, imgFile.getId().toString(), bookFile.getId().toString()));
    }

    /**
     * todo 到时候怎么说 向量数据库 + 关系数据库
     *  优化。。。。？？？
     * 对书籍文件进行page分页拆分写入数据库
     *
     * @param fileId
     */
    @SneakyThrows
    @Override
    @Async
    public void insertContestPages(Long fileId, Integer pages) {
        Files bookFile = filesService.getById(fileId);
        String filePath = bookFile.getFilePath();
//        System.out.println(filePath); //62d96c427fa5ad55c8100877e90383814cbd3fecd4408a989eb339473ac7f98b_《程序员的数学：线性代数》第三册.pdf
        if (!ossService.doesObjectExist(BucketConstant.COMMON_BUCKET_NAME, filePath)) {
            throw new BusinessException("文件不存在，请重现上传");
        }
        InputStream download = osService.download(BucketConstant.COMMON_BUCKET_NAME, filePath);
        byte[] bytes = download.readAllBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        FileOutputStream fileOutputStream = new FileOutputStream("./out.pdf");
        fileOutputStream.write(byteArrayInputStream.readAllBytes());
        fileOutputStream.flush();
        fileOutputStream.close();


        try (PDDocument pdDocument = Loader.loadPDF(bytes)) {
            int pageCount = pdDocument.getNumberOfPages();
            Long count = bookContentPageMapper.selectCount(new LambdaQueryWrapper<BookContentPage>()
                    .eq(BookContentPage::getBookId, fileId));
            if (count >= pageCount) {
                return;
            }
            bookContentPageMapper.delete(new LambdaQueryWrapper<BookContentPage>()
                    .eq(BookContentPage::getBookId, fileId)); //先删除所有
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            // 遍历每一页并提取内容
            ArrayList<BookContentPage> list = new ArrayList<>();
            BookContentPageMapper mapper = sqlSession.getMapper(BookContentPageMapper.class);
            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageContent = stripper.getText(pdDocument);
                // 去除无用字符
                pageContent = removeUselessCharactersByHanLP(pageContent);
                // 生成摘要
                String summary = generateSummary(pageContent);
                pageContent = summary +
                        "\n=========================\n"
                        + pageContent;
                BookContentPage bookContentPage = new BookContentPage();
                bookContentPage.setBookId(fileId);
                bookContentPage.setPage(page);
                bookContentPage.setContent(pageContent);
                list.add(bookContentPage);
                if (list.size() % 50 == 0) {
                    mapper.insertBatchSomeColumn(list);
                    list.clear();
                }
            }
            mapper.insertBatchSomeColumn(list);
            list.clear();
        }
    }

    @Override
    public BooksInfoHomePage getBooksInfoById(Long id) {
        return null;
    }

    @Override
    public BooksInfoReadingMode getBooksInfoReadingModeById(Long id, Integer pageNumber, Long userId) {
        return null;
    }

    @Override
    public void markBook(BookUnderlineCoordinates bookUnderlineCoordinates) {

    }


    private String removeUselessCharactersByHanLP(String text) {
        List<Term> termList = NLPTokenizer.segment(text);
        StringBuilder result = new StringBuilder();
        for (Term term : termList) {
            if (!isUselessTerm(term)) {
                result.append(term.word).append(" ");
            }
        }
        return result.toString().trim();
    }

    private String generateSummary(String text) {
        // 使用 HanLP 生成摘要
        List<String> phraseList = HanLP.extractPhrase(text, 10);
        System.out.println(phraseList);
        StringBuilder summary = new StringBuilder();
        for (String sentence : phraseList) {
            summary.append(sentence).append(" ");
        }
        return summary.toString().trim();
    }

    private boolean isUselessTerm(Term term) {
        // 这里可以根据词性等信息判断是否为无用词汇
        return term.nature.startsWith("w"); // 移除标点符号
    }

    /**
     * 默认第一页作为书籍封面
     *
     * @param inputStream
     *
     * @return
     * @throws Exception
     */
    public String convertFirstPageToImage(InputStream inputStream) throws Exception {
        String uuid = IdUtil.uuid();
        try (PDDocument pdDocument = Loader.loadPDF(inputStream.readAllBytes())) {
//         PDType1Font pdType1Font = new PDType1Font(Standard14Fonts.FontName.COURIER);
            PDPageTree pageTree = pdDocument.getPages();
            PDPage pdPage1 = pageTree.get(0);
            assert pdPage1 != null;
//            int firstNum = pageTree.indexOf(pdPage1) + 1;
            PDResources resources = pdPage1.getResources();
            for (COSName xObjectName : resources.getXObjectNames()) {
                PDXObject pdxObject = resources.getXObject(xObjectName);
                if (pdxObject instanceof PDImageXObject image) {
                    System.out.println("Found image with width "
                            + image.getWidth()
                            + "px and height "
                            + image.getHeight()
                            + "px.");

                    ImageIO.write(image.getImage(), "jpg", new File(uuid + ".jpg"));
                    File coverageFileImg = new File(uuid + ".jpg"); //首页作为封面
                    System.out.println(coverageFileImg.getAbsolutePath());
                    System.out.println(osService.toString());
                    try (FileInputStream fileInputStream = new FileInputStream(coverageFileImg)) {
                        return ossService.upload(BucketConstant.COMMON_BUCKET_NAME, fileInputStream, uuid + ".jpg");
                    }
                }
            }
        } finally {
            new Thread(() -> {
                cn.hutool.core.io.FileUtil.del(new File(uuid + "jpg"));
            }).start();
        }
        return null;
    }
}
