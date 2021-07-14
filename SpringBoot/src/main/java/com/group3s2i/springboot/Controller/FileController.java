package com.group3s2i.springboot.Controller;


import com.group3s2i.springboot.Config.response.UploadFileResponse;
import com.group3s2i.springboot.DAO.CustomerRepository;
import com.group3s2i.springboot.DAO.UserRepository;
import com.group3s2i.springboot.Model.Customer;
import com.group3s2i.springboot.Model.User;
import com.group3s2i.springboot.Service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author tfifha youssef
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @PutMapping("/uploadFile/customer/{id}")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,@PathVariable("id") long id) {
        System.out.println("Update User with ID = " + id + "...");
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isPresent()) {
            Customer utilisateur = customerOptional.get();
            utilisateur.setUrl (fileDownloadUri);
            customerRepository.save(utilisateur);
        }


        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}


