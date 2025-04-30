package org.ishareReading.bankai.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.pdfbox.text.PDFTextStripper;
import org.ishareReading.bankai.model.TocItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class BookUtils {

    private static final int MAX_LEVEL = 3;

    @SneakyThrows
    public Object getMetadata(byte[] bytes) {
        try (PDDocument pdDocument = Loader.loadPDF(bytes)) {
            int numberOfPages = pdDocument.getNumberOfPages();
            String bookMarks = convertBookmarksToJson(pdDocument);
            PDDocumentInformation documentInformation = pdDocument.getDocumentInformation();
            String author = documentInformation.getAuthor();
            String title = documentInformation.getTitle();
            Date time = documentInformation.getCreationDate().getTime();
            int year = time.getYear();
            PDDocumentCatalog documentCatalog = pdDocument.getDocumentCatalog();
            String language = documentCatalog.getLanguage();
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdDocument);

            // 提取 ISBN
//            String isbn = extractISBN(text);
            // 提取出版社
//            String publisher = extractPublisher(text);
            // 提取出版时间
//            String publicationDate = extractPublicationDate(text);
//    todo        图片大模型接口 + title + author ，
//            让大模型返回元数据
//            NLP 调优，进行这些分词等等
            return new MetaData(author, String.valueOf(numberOfPages), "",
                    "", "", year, title, language, bookMarks);
        }
    }

    /**
     * 获取三级目录json格式数组
     *
     * @param document
     *
     * @return
     * @throws IOException
     */
    public String convertBookmarksToJson(PDDocument document) throws IOException {
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        if (outline == null) {
            return "[]";
        }
        TocItem rootToc = new TocItem("Root", -1);
        buildTocTree(document, outline, rootToc, 0);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(rootToc.getChildren());
    }

    private void buildTocTree(PDDocument document, PDOutlineNode bookmark, TocItem parentToc, int level) {
        if (level >= MAX_LEVEL) {
            return;
        }
        PDOutlineItem current = bookmark.getFirstChild();
        while (current != null) {
            int page = getPageNumber(document, current);
            TocItem currentToc = new TocItem(current.getTitle(), page);
            parentToc.addChild(currentToc);
            buildTocTree(document, current, currentToc, level + 1);
            current = current.getNextSibling();
        }
    }

    @SneakyThrows
    private int getPageNumber(PDDocument document, PDOutlineItem current) {
        if (current.getDestination() instanceof PDPageDestination pd) {
            return pd.retrievePageNumber() + 1;
        } else if (current.getDestination() instanceof PDNamedDestination) {
            PDPageDestination pd = document.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) current.getDestination());
            if (pd != null) {
                return pd.retrievePageNumber() + 1;
            }
        } else if (current.getAction() instanceof PDActionGoTo gta) {
            if (gta.getDestination() instanceof PDPageDestination pd) {
                return pd.retrievePageNumber() + 1;
            } else if (gta.getDestination() instanceof PDNamedDestination) {
                PDPageDestination pd = document.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) gta.getDestination());
                if (pd != null) {
                    return pd.retrievePageNumber() + 1;
                }
            }
        }
        return -1;
    }

    //      publish_year
//        publisher
//        isbn
//        author
//        name
//        description
//        total_pages
//        language
    public record MetaData(String author, String pages, String description, String isbn,
                           String publisher, Integer publishDate, String title,
                           String language, String bookMarks) {

    }


}
