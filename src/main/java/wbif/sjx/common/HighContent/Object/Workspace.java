package wbif.sjx.common.HighContent.Object;

import java.io.File;
import java.util.HashMap;

/**
 * Created by sc13967 on 02/05/2017.
 */
public class Workspace {
    private HashMap<HCObjectName, HCObjectSet> objects = new HashMap<>();
    private HashMap<ImageName, Image> images = new HashMap<>();
    private Metadata metadata = new Metadata();
    private File currentFile = null;
    private int ID;

    // CONSTRUCTOR

    public Workspace(int ID, File currentFile) {
        this.ID = ID;
        this.currentFile = currentFile;

    }

    // PUBLIC METHODS

    public void addObjects(HCObjectName name, HCObjectSet object) {
        objects.put(name, object);
    }

    public void removeObject(String name) {
        objects.remove(name);
    }

    public void addImage(ImageName name, Image image) {
        images.put(name, image);
    }

    public void removeImage(String name) {
        images.remove(name);
    }

    public void clearAllImages() {
        images = null;
    }


    // GETTERS AND SETTERS

    public HashMap<HCObjectName, HCObjectSet> getObjects() {
        return objects;
    }

    public void setObjects(HashMap<HCObjectName, HCObjectSet> objects) {
        this.objects = objects;
    }

    public HashMap<ImageName, Image> getImages() {
        return images;
    }

    public void setImages(HashMap<ImageName, Image> images) {
        this.images = images;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public int getID() {
        return ID;
    }
}
