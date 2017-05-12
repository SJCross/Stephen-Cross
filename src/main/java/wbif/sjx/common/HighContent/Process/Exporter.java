package wbif.sjx.common.HighContent.Process;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import wbif.sjx.common.HighContent.Object.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by sc13967 on 12/05/2017.
 */
public class Exporter {
    public static final int XML_EXPORT = 0;
    public static final int XLSX_EXPORT = 1;
    public static final int JSON_EXPORT = 2;

    private int exportMode = XML_EXPORT;
    private File rootFolder;


    // CONSTRUCTOR

    public Exporter(File rootFolder, int exportMode) {
        this.rootFolder = rootFolder;
        this.exportMode = exportMode;

    }


    // PUBLIC METHODS

    public void exportResults(WorkspaceCollection workspaces) {
        exportResults(workspaces,null);

    }

    public void exportResults(WorkspaceCollection workspaces, ParameterCollection parameters) {
        if (exportMode == XML_EXPORT) {
            exportXML(workspaces,parameters);

        } else if (exportMode == XLSX_EXPORT) {
            exportXLSX(workspaces,parameters);

        } else if (exportMode == JSON_EXPORT) {
            exportJSON(workspaces,parameters);

        }
    }

    private void exportXML(WorkspaceCollection workspaces, ParameterCollection parameters) {
        // Initialising DecimalFormat
        DecimalFormat df = new DecimalFormat("0.000E0");

        try {
            // Initialising the document
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("ROOT");
            doc.appendChild(root);

            // Running through all parameters, adding them if present
            if (parameters != null) {
                Element parametersElement =  doc.createElement("PARAMETERS");

                // Running through each paramter set (one for each module
                for (Integer key:parameters.getParameters().keySet()) {
                    HashMap<String,Parameter> moduleParameters = parameters.getParameters().get(key);

                    boolean first = true;
                    Element moduleElement =  doc.createElement("MODULE");
                    for (Parameter currParam:moduleParameters.values()) {
                        // For the first parameter in a module, adding the name
                        if (first) {
                            Attr nameAttr = doc.createAttribute("NAME");
                            nameAttr.appendChild(doc.createTextNode(currParam.getModule().getClass().getName()));
                            moduleElement.setAttributeNode(nameAttr);

                            Attr hashAttr = doc.createAttribute("HASH");
                            hashAttr.appendChild(doc.createTextNode(String.valueOf(currParam.getModule().hashCode())));
                            moduleElement.setAttributeNode(hashAttr);

                            first = false;
                        }

                        // Adding the name and value of the current parameter
                        Element parameterElement =  doc.createElement("PARAMETER");

                        Attr nameAttr = doc.createAttribute("NAME");
                        nameAttr.appendChild(doc.createTextNode(currParam.getName()));
                        parameterElement.setAttributeNode(nameAttr);

                        Attr valueAttr = doc.createAttribute("VALUE");
                        valueAttr.appendChild(doc.createTextNode(currParam.getValue().toString()));
                        parameterElement.setAttributeNode(valueAttr);

                        moduleElement.appendChild(parameterElement);

                    }

                    // Adding current module to parameters
                    parametersElement.appendChild(moduleElement);

                }

                // Adding parameters to main file
                root.appendChild(parametersElement);

            }

            // Running through each workspace (each corresponds to a file) adding file information
            for (Workspace workspace:workspaces) {
                Element setElement =  doc.createElement("SET");

                // Adding metadata from the workspace
                Metadata metadata = workspace.getMetadata();
                for (String key:metadata.keySet()) {
                    String attrName = key.toUpperCase();
                    Attr attr = doc.createAttribute(attrName);
                    attr.appendChild(doc.createTextNode(metadata.getAsString(key)));
                    setElement.setAttributeNode(attr);

                }

                // Creating new elements for each image in the current workspace with at least one measurement
                for (ImageName imageName:workspace.getImages().keySet()) {
                    Image image = workspace.getImages().get(imageName);

                    if (image.getMeasurements() != null) {
                        Element imageElement = doc.createElement("IMAGE");

                        Attr nameAttr = doc.createAttribute("NAME");
                        nameAttr.appendChild(doc.createTextNode(String.valueOf(imageName.getName())));
                        imageElement.setAttributeNode(nameAttr);

                        for (Measurement measurement : image.getMeasurements().values()) {
                            String attrName = measurement.getName().toUpperCase().replaceAll(" ", "_");
                            Attr measAttr = doc.createAttribute(attrName);
                            String attrValue = df.format(measurement.getValue());
                            measAttr.appendChild(doc.createTextNode(attrValue));
                            imageElement.setAttributeNode(measAttr);
                        }

                        setElement.appendChild(imageElement);

                    }
                }

                // Creating new elements for each object in the current workspace
                for (HCObjectName objectNames:workspace.getObjects().keySet()) {
                    for (HCObject object:workspace.getObjects().get(objectNames).values()) {
                        Element objectElement =  doc.createElement("OBJECT");

                        // Setting the ID number
                        Attr idAttr = doc.createAttribute("ID");
                        idAttr.appendChild(doc.createTextNode(String.valueOf(object.getID())));
                        objectElement.setAttributeNode(idAttr);

                        Attr nameAttr = doc.createAttribute("NAME");
                        nameAttr.appendChild(doc.createTextNode(String.valueOf(objectNames.getName())));
                        objectElement.setAttributeNode(nameAttr);

                        for (Measurement measurement:object.getMeasurements().values()) {
                            String attrName = measurement.getName().toUpperCase().replaceAll(" ", "_");
                            Attr measAttr = doc.createAttribute(attrName);
                            String attrValue = df.format(measurement.getValue());
                            measAttr.appendChild(doc.createTextNode(attrValue));
                            objectElement.setAttributeNode(measAttr);
                        }

                        setElement.appendChild(objectElement);

                    }
                }

                root.appendChild(setElement);

            }

            // Preparing the filepath and filename
            String outPath = rootFolder+"\\"+"output.xml";

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outPath);

            transformer.transform(source, result);

            System.out.println("Saved "+ outPath);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void exportXLSX(WorkspaceCollection workspaces, ParameterCollection parameters) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Creating a sheet for parameters
        XSSFSheet paramSheet = workbook.createSheet("Parameters");

        // Adding a header row for the paramter titles
        int paramRow = 0;
        int paramCol = 0;
        Row parameterHeader = paramSheet.createRow(paramRow++);

        Cell nameHeaderCell = parameterHeader.createCell(paramCol++);
        nameHeaderCell.setCellValue("PARAMETER");

        Cell valueHeaderCell = parameterHeader.createCell(paramCol++);
        valueHeaderCell.setCellValue("VALUE");

        Cell moduleHeaderCell = parameterHeader.createCell(paramCol++);
        moduleHeaderCell.setCellValue("MODULE");

//        Cell moduleHashHeaderCell = parameterHeader.createCell(paramCol);
//        moduleHashHeaderCell.setCellValue("MODULE_HASH");

        // Adding a new parameter to each row
        int currentHash = 0;
        for (Integer key:parameters.getParameters().keySet()) {
            LinkedHashMap<String,Parameter> currentParameters = parameters.getParameters().get(key);

            for (Parameter currParam:currentParameters.values()) {
                paramCol = 0;
                Row row = paramSheet.createRow(paramRow++);

                Cell nameValueCell = row.createCell(paramCol++);
                nameValueCell.setCellValue(currParam.getName());

                Cell valueValueCell = row.createCell(paramCol++);
                valueValueCell.setCellValue(currParam.getValue().toString());

                Cell moduleValueCell = row.createCell(paramCol++);
                moduleValueCell.setCellValue(currParam.getModule().getClass().getName());

//                Cell moduleHashValueCell = row.createCell(paramCol);
//                moduleHashValueCell.setCellValue(currParam.getModule().hashCode());

                // Adding a blank line between parameters from different modules
                if (currentHash != currParam.getModule().hashCode()) {
                    currentHash = currParam.getModule().hashCode();
                    paramRow++;
                }

            }

        }

        // Creating a new sheet for each image.  Each analysed file will have its own row.
        Workspace exampleWorkspace = workspaces.get(0);
        HashMap<ImageName,XSSFSheet> imageSheets = new HashMap<>();
        HashMap<ImageName,Integer> imageRows = new HashMap<>();

        // Using the first workspace in the WorkspaceCollection to initialise column headers
        for (ImageName imageName:exampleWorkspace.getImages().keySet()) {
            Image image = exampleWorkspace.getImages().get(imageName);

            // Creating relevant sheet prefixed with "IM"
            imageSheets.put(imageName,workbook.createSheet("IM_"+imageName.getName()));

            // Adding headers to each column
            int col = 0;

            imageRows.put(imageName,1);
            Row imageHeaderRow = imageSheets.get(imageName).createRow(0);

            // Creating a cell holding the path to the analysed file
            Cell IDHeaderCell = imageHeaderRow.createCell(col++);
            IDHeaderCell.setCellValue("ANALYSIS_ID");

            for (Measurement measurement:image.getMeasurements().values()) {
                Cell measHeaderCell = imageHeaderRow.createCell(col++);
                String measurementName = measurement.getName().toUpperCase().replaceAll(" ", "_");
                measHeaderCell.setCellValue(measurementName);
            }
        }

        // Running through each Workspace, adding rows
        for (Workspace workspace:workspaces) {
            for (ImageName imageName:workspace.getImages().keySet()) {
                Image image = exampleWorkspace.getImages().get(imageName);

                // Adding the measurements from this image
                int col = 0;

                Row imageValueRow = imageSheets.get(imageName).createRow(imageRows.get(imageName));
                imageRows.compute(imageName,(k,v) -> v = v + 1);

                // Creating a cell holding the path to the analysed file
                Cell IDValueCell = imageValueRow.createCell(col++);
                IDValueCell.setCellValue(workspace.getID());

                for (Measurement measurement:image.getMeasurements().values()) {
                    Cell measValueCell = imageValueRow.createCell(col++);
                    measValueCell.setCellValue(measurement.getValue());
                }
            }
        }

        // Creating a new sheet for each image.  Each analysed file will have its own row.
        HashMap<HCObjectName,XSSFSheet> objectSheets = new HashMap<>();
        HashMap<HCObjectName,Integer> objectRows = new HashMap<>();

        // Using the first workspace in the WorkspaceCollection to initialise column headers
        for (HCObjectName objectName:exampleWorkspace.getObjects().keySet()) {
            HashMap<Integer,HCObject> objects = exampleWorkspace.getObjects().get(objectName);

            // Creating relevant sheet prefixed with "IM"
            objectSheets.put(objectName,workbook.createSheet("OBJ_"+objectName.getName()));

            // Adding headers to each column
            int col = 0;

            objectRows.put(objectName,1);
            Row objectHeaderRow = objectSheets.get(objectName).createRow(0);

            // Creating a cell holding the path to the analysed file
            Cell IDHeaderCell = objectHeaderRow.createCell(col++);
            IDHeaderCell.setCellValue("ANALYSIS_ID");

            HCObject object = objects.values().iterator().next();
            for (Measurement measurement:object.getMeasurements().values()) {
                Cell measHeaderCell = objectHeaderRow.createCell(col++);
                String measurementName = measurement.getName().toUpperCase().replaceAll(" ", "_");
                measHeaderCell.setCellValue(measurementName);
            }
        }

        // Running through each Workspace, adding rows
        for (Workspace workspace:workspaces) {
            for (HCObjectName objectName:exampleWorkspace.getObjects().keySet()) {
                HashMap<Integer,HCObject> objects = exampleWorkspace.getObjects().get(objectName);

                for (HCObject object:objects.values()) {
                    // Adding the measurements from this image
                    int col = 0;

                    Row objectValueRow = objectSheets.get(objectName).createRow(objectRows.get(objectName));
                    objectRows.compute(objectName, (k, v) -> v = v + 1);
                    System.out.println(objectRows.get(objectName));

                    // Creating a cell holding the path to the analysed file
                    Cell IDValueCell = objectValueRow.createCell(col++);
                    IDValueCell.setCellValue(workspace.getID());

                    for (Measurement measurement : object.getMeasurements().values()) {
                        Cell measValueCell = objectValueRow.createCell(col++);
                        measValueCell.setCellValue(measurement.getValue());
                    }
                }
            }
        }

//        for (ImageName imageName:workspace.getImages().keySet()) {
//
//            workbook.createSheet("Parameters");
//            Image image = workspace.getImages().get(imageName);
//
//            if (image.getMeasurements() != null) {
//                Element imageElement = doc.createElement("IMAGE");
//
//                Attr nameAttr = doc.createAttribute("NAME");
//                nameAttr.appendChild(doc.createTextNode(String.valueOf(imageName.getName())));
//                imageElement.setAttributeNode(nameAttr);
//
//                for (Measurement measurement : image.getMeasurements().values()) {
//                    String attrName = measurement.getName().toUpperCase().replaceAll(" ", "_");
//                    Attr measAttr = doc.createAttribute(attrName);
//                    String attrValue = df.format(measurement.getValue());
//                    measAttr.appendChild(doc.createTextNode(attrValue));
//                    imageElement.setAttributeNode(measAttr);
//                }
//
//                setElement.appendChild(imageElement);
//
//            }
//        }
//
//        // Creating new elements for each object in the current workspace
//        for (HCObjectName objectNames:workspace.getObjects().keySet()) {
//            for (HCObject object:workspace.getObjects().get(objectNames).values()) {
//                Element objectElement =  doc.createElement("OBJECT");
//
//                // Setting the ID number
//                Attr idAttr = doc.createAttribute("ID");
//                idAttr.appendChild(doc.createTextNode(String.valueOf(object.getID())));
//                objectElement.setAttributeNode(idAttr);
//
//                Attr nameAttr = doc.createAttribute("NAME");
//                nameAttr.appendChild(doc.createTextNode(String.valueOf(objectNames.getName())));
//                objectElement.setAttributeNode(nameAttr);
//
//                for (Measurement measurement:object.getMeasurements().values()) {
//                    String attrName = measurement.getName().toUpperCase().replaceAll(" ", "_");
//                    Attr measAttr = doc.createAttribute(attrName);
//                    String attrValue = df.format(measurement.getValue());
//                    measAttr.appendChild(doc.createTextNode(attrValue));
//                    objectElement.setAttributeNode(measAttr);
//                }
//
//                setElement.appendChild(objectElement);
//
//            }
//        }



        try {
            FileOutputStream outputStream = new FileOutputStream(rootFolder+"\\"+"output.xlsx");
            workbook.write(outputStream);
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void exportJSON(WorkspaceCollection workspaces, ParameterCollection parameters) {
        System.out.println("[WARN] No JSON export currently implemented.  File not saved.");

    }

}
