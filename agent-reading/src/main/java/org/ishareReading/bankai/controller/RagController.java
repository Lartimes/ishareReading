package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.service.rag.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rag")
public class RagController {

    @Autowired
    private RagService ragService;

    @PostMapping("/importResources")
    public void importResources(@RequestPart MultipartFile file) {
        ragService.importDocuments(file.getResource());
    }
}
