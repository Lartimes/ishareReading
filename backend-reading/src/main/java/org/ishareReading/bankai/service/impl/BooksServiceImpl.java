package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
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
import org.ishare.oss.OssService;
import org.ishareReading.bankai.constant.BucketConstant;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.mapper.BookContentPageMapper;
import org.ishareReading.bankai.mapper.BooksMapper;
import org.ishareReading.bankai.model.*;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.*;
import org.ishareReading.bankai.utils.BookUtils;
import org.ishareReading.bankai.utils.FileUtil;
import org.ishareReading.bankai.utils.IdUtil;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    // 自定义线程池
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final RedisTemplate redisTemplate;
    private final OssService ossService;
    private final FilesService filesService;
    private final OssService osService;
    private final BookContentPageMapper bookContentPageMapper;
    private final BookUtils bookUtils;
    private final ObjectMapper objectMapper;
    private final SqlSession sqlSession;
    private final BookUnderlineCoordinatesService bookUnderlineCoordinatesService;
    private final BookOpinionsService bookOpinionsService;
    private final RedisCacheUtil redisCacheUtil;
    private final BookContentPageService bookContentPageService;

    public BooksServiceImpl(OssService ossService, RedisTemplate redisTemplate, FilesService filesService, OssService osService,
                            BookContentPageMapper bookContentPageMapper, BookUtils bookUtils, ObjectMapper objectMapper,
                            SqlSession sqlSession, BookUnderlineCoordinatesService bookUnderlineCoordinatesService,
                            BookOpinionsService bookOpinionsService,
                            RedisCacheUtil redisCacheUtil, BookContentPageService bookContentPageService) {
        this.ossService = ossService;
        this.redisTemplate = redisTemplate;
        this.filesService = filesService;
        this.osService = osService;
        this.bookContentPageMapper = bookContentPageMapper;
        this.bookUtils = bookUtils;
        this.objectMapper = objectMapper;
        this.sqlSession = sqlSession;
        this.bookUnderlineCoordinatesService = bookUnderlineCoordinatesService;
        this.bookOpinionsService = bookOpinionsService;
        this.redisCacheUtil = redisCacheUtil;
        this.bookContentPageService = bookContentPageService;
    }

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
        books.setUploadTime(LocalDateTime.now());
        boolean b = this.save(books);
        Long pk = books.getId();
        new Thread(() -> {
//            ByteArrayInputStream pdfContent = new ByteArrayInputStream(bytes);
            //TODO 异步进行数据内容处理？？、
            if (b) {
                this.insertContestPages(pk, books.getFileId(), books.getTotalPages());
                //更新系统书籍类型库
                if (books.getGenre() != null) {
                    String[] genreSplit = books.getGenreSplit();
                    byte[] value = String.valueOf(books.getId()).getBytes(StandardCharsets.UTF_8);
                    redisCacheUtil.pipeline((connection -> {
                        for (String label : genreSplit) {
                            connection.zSetCommands().zAdd((RedisConstant.BOOK_TYPE + label).getBytes(),
                                    System.currentTimeMillis(), value);
                        }
                        return null;
                    }));
                }

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
        String contentType = multipartFile.getContentType();
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = FileUtil.getExtension(originalFilename);
        String dbType = FileUtil.detectFileType(multipartFile);

        byte[] bytes = multipartFile.getInputStream().readAllBytes();

        CompletableFuture<String> coverageIdFuture = CompletableFuture.supplyAsync(() -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                return convertFirstPageToImage(bis);
            } catch (Exception e) {
                System.err.println("Error converting first page to image: " + e.getMessage());
                return null;
            }
        }, EXECUTOR_SERVICE);
        CompletableFuture<Object> metadataFuture = CompletableFuture.supplyAsync(() -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                return bookUtils.getMetadata(bis);
            } catch (Exception e) {
                System.err.println("Error getting file metadata: " + e.getMessage());
                return null;
            }
        }, EXECUTOR_SERVICE);

        CompletableFuture<String> uploadFuture = CompletableFuture.supplyAsync(() -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                return ossService.upload(BucketConstant.COMMON_BUCKET_NAME, bis, originalFilename);
            } catch (IOException e) {
                System.err.println("Error uploading file: " + e.getMessage());
                return null;
            }
        }, EXECUTOR_SERVICE);
        Files bookFile = new Files();
        bookFile.setSize(multipartFile.getSize());
        bookFile.setType(dbType);
        bookFile.setFormat(contentType);
        bookFile.setFileName(originalFilename);
        bookFile.setFilePath(uploadFuture.get());
        bookFile.setExtension(extension);
        bookFile.setUserId(UserHolder.get());
        filesService.save(bookFile);

        Files imgFile = new Files();
        imgFile.setFormat("image/jpeg");
        imgFile.setType("图片");
        imgFile.setFileName(Objects.requireNonNull(originalFilename).substring(0, originalFilename.lastIndexOf(".") + 1) + ".jpg");
        imgFile.setFilePath(coverageIdFuture.get());
        imgFile.setExtension("jpg");
        imgFile.setUserId(UserHolder.get());
        filesService.save(imgFile);

        Object metadata = metadataFuture.get();

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
    public void insertContestPages(Long pk, Long fileId, Integer pages) {
        Files bookFile = filesService.getById(fileId);
        String filePath = bookFile.getFilePath();
//        System.out.println(filePath); //62d96c427fa5ad55c8100877e90383814cbd3fecd4408a989eb339473ac7f98b_《程序员的数学：线性代数》第三册.pdf
        if (!ossService.doesObjectExist(BucketConstant.COMMON_BUCKET_NAME, filePath)) {
            throw new BusinessException("文件不存在，请重现上传");
        }
        InputStream download = osService.download(BucketConstant.COMMON_BUCKET_NAME, filePath);
        byte[] bytes = download.readAllBytes();
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
//                pageContent = removeUselessCharactersByHanLP(pageContent);
                // 生成摘要
                String summary = generateSummary(pageContent);
                pageContent = summary +
                        "\n=========================\n"
                        + pageContent;
                BookContentPage bookContentPage = new BookContentPage();
                bookContentPage.setBookId(pk);
                bookContentPage.setPage(page);
                bookContentPage.setContent(pageContent);
                PDPage pdPage = pdDocument.getPage(page - 1);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bookContentPage.setPdfPageStream(Base64.encodeBase64String(getStreamByPage(pdPage, byteArrayOutputStream)));
                list.add(bookContentPage);
                if (list.size() % 50 == 0) {
                    bookContentPageService.saveBatch(list);
//                    mapper.insertBatchSomeColumn(list);
                    list.clear();
                }
            }
            bookContentPageService.saveBatch(list);
//            mapper.insertBatchSomeColumn(list);
            list.clear();
        }
//        Thread.sleep(1000L); //等一下事务结束，非必须
    }


    /**
     * 根据书籍ID 获取书籍门户首页信息 * 这里先SQL ， 后期接入ES *  bookinfo *  封面 *  当个书籍的热门评论
     *
     * @param id
     *
     * @return
     */
    @SneakyThrows
    @Override
    public BooksInfoHomePage getBooksInfoById(Long id) {
        Books book = this.getById(id);
        book.setViewCount(book.getViewCount() == null ? 1 : book.getViewCount() + 1);
        Long fileId = book.getFileId();
        Long coverImageId = book.getCoverImageId(); //封面id？
        Files coverage = filesService.getById(coverImageId);
        byte[] coverageByes = null;
        String coverageBase64 = null;
//        ==================================获取封面信息
        if (!ossService.doesObjectExist(BucketConstant.COMMON_BUCKET_NAME, coverage.getFilePath())) {
            //如果不存在的话？ 直接去contents表获取第一页-1数据，直接返回
            BookContentPage bookContentPage = bookContentPageMapper.selectOne(new LambdaQueryWrapper<BookContentPage>()
                    .eq(BookContentPage::getBookId, fileId)
                    .eq(BookContentPage::getPage, 0));
            coverageBase64 = bookContentPage.getPdfPageStream();//进行base64编码
        } else {
            coverageByes = osService.download(BucketConstant.COMMON_BUCKET_NAME, coverage.getFilePath()).readAllBytes();
            coverageBase64 = Base64.encodeBase64String(coverageByes);
        }

//        ===================================获取热门评论点评
        List<BookOpinions> list = bookOpinionsService.list(new LambdaQueryWrapper<BookOpinions>()
                .eq(BookOpinions::getBookId, fileId)
                .isNull(BookOpinions::getUnderlinedId)
                .orderByDesc(BookOpinions::getLikeCount, BookOpinions::getCreateAt)
                .last("limit 5")); //默认五条，我们整个系统不做分页了
        new Thread(() -> {
            this.updateById(book);
        }).start();
        return new BooksService.BooksInfoHomePage(book, coverageBase64, list, 0);
    }

    //    TODO 这些返回结果都应该再带一个type
    //        书籍模式根据页数 + id 获取内容
//    图片内容
//    AI鉴赏
//    用户标注
    @Override
    public BooksInfoReadingMode getBooksInfoReadingModeById(Long id, Integer pageNumber, Long userId) {
        Books book = this.getById(id);
        BookContentPage bookContentPage = bookContentPageMapper.selectOne(new LambdaQueryWrapper<BookContentPage>()
                .eq(BookContentPage::getBookId, id)
                .eq(BookContentPage::getPage, pageNumber));
        String pageBase64 = bookContentPage.getPdfPageStream();//进行base64编码
        //===========================获取当前页数
//        获取本页所有下划线标注
        List<BookUnderlineCoordinates> list = bookUnderlineCoordinatesService.list(new LambdaQueryWrapper<BookUnderlineCoordinates>()
                .eq(BookUnderlineCoordinates::getBookId, id)
                .eq(BookUnderlineCoordinates::getPageNumber, pageNumber));
        Set<Long> collect = null;
        Set<Long> underlines = null;
        if (!(list == null || list.isEmpty())) {
            collect = list.parallelStream().map(BaseModel::getId).collect(Collectors.toSet());
            Set<Long> underlinesSelf =
                    list.parallelStream().filter(a -> userId.equals(a.getUserId()))
                            .map(BookUnderlineCoordinates::getId).collect(Collectors.toSet());
            collect.removeAll(underlinesSelf);
            underlines = underlinesSelf;
        }

//       ========================= 必须要显示的
        List<BookOpinions> hotPublicOpinions = bookOpinionsService.list(new LambdaQueryWrapper<BookOpinions>()
                .eq(BookOpinions::getBookId, id)
                .in(collect != null, BookOpinions::getUnderlinedId, collect)
                .orderByDesc(BookOpinions::getLikeCount, BookOpinions::getCreateAt)
                .last("limit 10")); //大众化的
        List<BookOpinions> selfOpinions = bookOpinionsService.list(new LambdaQueryWrapper<BookOpinions>()
                .eq(BookOpinions::getBookId, id)
                .eq(BookOpinions::getUserId, userId)
                .in(underlines != null, BookOpinions::getUnderlinedId, underlines)); //个人标注 + 也可能是hot
//      ==================================  统一进行处理，  查询出对该标注的评论
        hotPublicOpinions.addAll(selfOpinions);
//        todo 这里要进行标注内容比对，进行分类，只筛选，hanlp 做语义相似度解析
        return new BooksService.BooksInfoReadingMode(book, pageBase64, pageNumber, hotPublicOpinions);
    }

    @Override
    public void markBook(BookUnderlineCoordinates bookUnderlineCoordinates) {
        Long bookId = bookUnderlineCoordinates.getBookId();
        String content = bookUnderlineCoordinates.getContent();
        Long userId = UserHolder.get();
        bookUnderlineCoordinates.setUserId(userId);
        bookUnderlineCoordinatesService.save(bookUnderlineCoordinates);
        Long id = bookUnderlineCoordinates.getId();
        BookOpinions bookOpinions = new BookOpinions();
        bookOpinions.setBookId(bookId);
        bookOpinions.setUserId(userId);
        bookOpinions.setUnderlinedId(id);
        bookOpinions.setOpinionText(content);
        bookOpinionsService.save(bookOpinions);
    }

    @SneakyThrows
    @Override
    public List<BooksInfoHomePage> convert2HomePage(Collection<Books> books) {
        int size = books.size();
        ArrayList<BooksInfoHomePage> result = new ArrayList<>(size);
        for (Books book : books) {
            byte[] coverageByes = null;
            String coverageBase64 = null;
            Long coverImageId = book.getCoverImageId();
            Files coverage = filesService.getById(coverImageId);
            if (coverage.getFilePath() != null) {
                if (!ossService.doesObjectExist(BucketConstant.COMMON_BUCKET_NAME, coverage.getFilePath())) {
                    BookContentPage bookContentPage = bookContentPageMapper.selectOne(new LambdaQueryWrapper<BookContentPage>()
                            .eq(BookContentPage::getBookId, book.getId())
                            .eq(BookContentPage::getPage, 0));
                    if (bookContentPage == null) {
                        continue;
                    }
                    coverageBase64 = bookContentPage.getPdfPageStream();
                } else {
                    coverageByes = osService.download(BucketConstant.COMMON_BUCKET_NAME, coverage.getFilePath()).readAllBytes();
                    coverageBase64 = Base64.encodeBase64String(coverageByes);
                }
                result.add(new BooksService.BooksInfoHomePage(book, coverageBase64, null, null));
            }

        }
        return result;
    }

    @Override
    public List<Books> getRecentlyReleaseBook() {
        return list(new LambdaQueryWrapper<Books>().orderByDesc(Books::getUploadTime).last("limit 5"));
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

    private String generateSummary(String text) {
        // 使用 HanLP 生成摘要
        List<String> phraseList = HanLP.extractPhrase(text, 10);
        StringBuilder summary = new StringBuilder();
        for (String sentence : phraseList) {
            summary.append(sentence).append(" ");
        }
        return summary.toString().trim();
    }

    @SneakyThrows
    private byte[] getStreamByPage(PDPage pdPage, ByteArrayOutputStream baos) {
        PDResources resources = pdPage.getResources();
        for (COSName xObjectName : resources.getXObjectNames()) {
            PDXObject pdxObject = resources.getXObject(xObjectName);
            if (pdxObject instanceof PDImageXObject image) {
//                System.out.println("Found image with width "
//                        + image.getWidth()
//                        + "px and height "
//                        + image.getHeight()
//                        + "px.");
                BufferedImage bufferedImage = image.getImage();
                ImageIO.write(bufferedImage, "png", baos);
                baos.flush();
                return baos.toByteArray();
            }
        }
        return null;
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

    private boolean isUselessTerm(Term term) {
        // 这里可以根据词性等信息判断是否为无用词汇
        return term.nature.startsWith("w"); // 移除标点符号
    }
}
