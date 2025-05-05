package org.ishareReading.bankai.utils;

import cn.hutool.core.date.DateUtil;
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
import org.ishareReading.bankai.model.TocItem;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@Component
public class BookUtils {

    private static final int MAX_LEVEL = 3;

    @SneakyThrows
    public Object getMetadata(ByteArrayInputStream inputStream) {
        try (PDDocument pdDocument = Loader.loadPDF(inputStream.readAllBytes())) {
            // 获取页面数量
            int numberOfPages = pdDocument.getNumberOfPages();
            PDDocumentInformation documentInformation = pdDocument.getDocumentInformation();
            String author = documentInformation.getAuthor();
            String title = documentInformation.getTitle();
            Date creationDate = documentInformation.getCreationDate().getTime();
            int year = DateUtil.year(creationDate);
            PDDocumentCatalog documentCatalog = pdDocument.getDocumentCatalog();
            String language = documentCatalog.getLanguage();
//            PDFTextStripper stripper = new PDFTextStripper();
            // 可根据需要设置提取范围
            // stripper.setStartPage(1);
            // stripper.setEndPage(numberOfPages);
//            String text = stripper.getText(pdDocument);

            // 提取 ISBN、出版社、出版时间等信息
            // String isbn = extractISBN(text);
            // String publisher = extractPublisher(text);
            // String publicationDate = extractPublicationDate(text);
//            String s = convertBookmarksToJson(pdDocument); //获取目录结构
            String s = convertBookmarksToTree(pdDocument);
            return new MetaData(author, String.valueOf(numberOfPages), "", "",
                    "", year, title, language, s);
        } catch (IOException e) {
            // 处理异常
            System.err.println("Error extracting PDF metadata: " + e.getMessage());
            return null;
        }
    }

    @SneakyThrows
    public String convertToBookDir(ByteArrayInputStream inputStream) {
        try (PDDocument pdDocument = Loader.loadPDF(inputStream.readAllBytes())) {
            return convertBookmarksToJson(pdDocument);
        }
    }


    public static String convertBookmarksToTree(PDDocument document) throws IOException {
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        if (outline == null) {
            return "";
        }
        StringBuilder treeText = new StringBuilder();
        buildTreeText(outline.getFirstChild(), document, treeText, 0);
        return treeText.toString();
    }

    private static void buildTreeText(PDOutlineItem item, PDDocument document, StringBuilder treeText, int level) {
        if (level >= MAX_LEVEL || item == null) {
            return;
        }
        String indent = "  ".repeat(level);
        int page = getPageNumber(item, document);
        treeText.append(indent).append(item.getTitle());
        if (page != -1) {
            treeText.append(" (Page ").append(page).append(")");
        }
        treeText.append("\n");

        buildTreeText(item.getFirstChild(), document, treeText, level + 1);
        buildTreeText(item.getNextSibling(), document, treeText, level);
    }

    private static int getPageNumber(PDOutlineItem item, PDDocument document) {
        try {
            return document.getPages().indexOf(item.findDestinationPage(document)) + 1;
        } catch (IOException e) {
            return -1;
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
//        language 目录
    public record MetaData(String author, String pages, String description, String isbn,
                           String publisher, Integer publishDate, String title,
                           String language, String bookMarks) {

    }


}
