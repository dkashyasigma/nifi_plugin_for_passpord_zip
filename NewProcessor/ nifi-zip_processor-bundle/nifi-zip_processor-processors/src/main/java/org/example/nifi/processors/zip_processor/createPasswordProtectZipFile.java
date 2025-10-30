package org.example.nifi.processors.zip_processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

@Tags({"zip", "password", "encryption", "compression"})
@CapabilityDescription("Creates a password protected ZIP file from the FlowFile content using the Zip4j library")
@ReadsAttribute(attribute = "filename", description = "Filename attribute is used to name the temporary file for zipping")
@WritesAttribute(attribute = "zip.filename", description = "The name of the generated zip file")
public class createPasswordProtectZipFile extends AbstractProcessor {

    public static final PropertyDescriptor PASSWORD = new PropertyDescriptor.Builder()
            .name("Password")
            .displayName("Password")
            .description("Password used to encrypt the ZIP file")
            .required(true)
            .sensitive(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully created password protected zip file")
            .build();

    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("Failed to create password protected zip file")
            .build();

    private List<PropertyDescriptor> descriptors;
    private Set<Relationship> relationships;

    @Override
    protected void init(final org.apache.nifi.processor.ProcessorInitializationContext context) {
        descriptors = List.of(PASSWORD);
        relationships = Set.of(REL_SUCCESS, REL_FAILURE);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {
        // No additional scheduling required
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }

        final String password = context.getProperty(PASSWORD).getValue();

        final File[] tempInputFile = new File[1];
        File tempZipFile = null;

        try {
            String inputFileName = flowFile.getAttribute("filename");
            if (inputFileName == null) {
                inputFileName = "inputfile";
            }

            // Create temporary file for input content
            tempInputFile[0] = File.createTempFile("input", "_" + inputFileName);

            // Create output zip file name (basename + .zip)
            String baseName = inputFileName;
            int dotIndex = inputFileName.lastIndexOf(".");
            if (dotIndex > 0) {
                baseName = inputFileName.substring(0, dotIndex);
            }
            getLogger().info("zip file base name from ", inputFileName," to ",baseName);
            tempZipFile = new File(tempInputFile[0].getParentFile(), inputFileName + ".zip");
            // Write FlowFile content to temp input file
            session.read(flowFile, (InputStream in) -> {
                try (FileOutputStream fos = new FileOutputStream(tempInputFile[0])) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
            });

            // Use Zip4j to create password protected ZIP
            ZipFile zipFile = new ZipFile(tempZipFile, password.toCharArray());

            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);

            // Set the filename to be used inside the zip archive
            zipParameters.setFileNameInZip(inputFileName);

            zipFile.addFile(tempInputFile[0], zipParameters);
             // Add the input file content as a stream with the correct filename inside the ZIP
            // try (InputStream fileInputStream = new FileInputStream(tempInputFile)) {
            //     zipFile.addStream(fileInputStream, zipParameters);
            // }


            // Create new FlowFile for zipped content
            FlowFile zippedFlowFile = session.create(flowFile);

            // Import zipped file content into FlowFile using InputStream (fix for importFrom)
            try (InputStream zipInputStream = new FileInputStream(tempZipFile)) {
                zippedFlowFile = session.importFrom(zipInputStream, zippedFlowFile);
            }

            // Set attribute indicating zipped filename
            zippedFlowFile = session.putAttribute(zippedFlowFile, "zip.filename", tempZipFile.getName());

            // Transfer zipped FlowFile to success
            session.transfer(zippedFlowFile, REL_SUCCESS);

            // Remove original FlowFile
            session.remove(flowFile);

        } catch (Exception e) {
            getLogger().error("Failed to create password protected zip for {}", new Object[]{flowFile}, e);
            session.transfer(flowFile, REL_FAILURE);
        } finally {
            // Clean up temp files
            if (tempInputFile[0] != null && tempInputFile[0].exists()) {
                tempInputFile[0].delete();
            }
            if (tempZipFile != null && tempZipFile.exists()) {
                tempZipFile.delete();
            }
        }
    }
}
