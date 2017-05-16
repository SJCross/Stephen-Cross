package wbif.sjx.common.HighContent.Module;

import ij.ImagePlus;
import wbif.sjx.common.Analysis.IntensityCalculator;
import wbif.sjx.common.HighContent.Object.*;
import wbif.sjx.common.MathFunc.CumStat;

/**
 * Created by sc13967 on 12/05/2017.
 */
public class MeasureImageIntensity extends HCModule {
    public static final String INPUT_IMAGE = "Input image";

    @Override
    public void execute(HCWorkspace workspace, boolean verbose) {
        if (verbose) System.out.println("   Measuring image intensity");

        // Getting input image
        HCImageName inputImageName = parameters.getValue(INPUT_IMAGE);
        if (verbose) System.out.println("       Loading image ("+inputImageName+")");
        HCImage inputImage = workspace.getImages().get(inputImageName);
        ImagePlus inputImagePlus = inputImage.getImagePlus();

        // Running measurement
        CumStat cs = IntensityCalculator.calculate(inputImagePlus);

        // Adding measurements to image
        HCSingleMeasurement meanIntensity = new HCSingleMeasurement(HCSingleMeasurement.MEAN_INTENSITY,cs.getMean()[0]);
        meanIntensity.setSource(this);
        inputImage.addMeasurement(meanIntensity.getName(),meanIntensity);
        if (verbose) System.out.println("       Mean intensity = "+meanIntensity.getValue());

        HCSingleMeasurement stdIntensity = new HCSingleMeasurement(HCSingleMeasurement.STD_INTENSITY,cs.getStd(CumStat.SAMPLE)[0]);
        stdIntensity.setSource(this);
        inputImage.addMeasurement(stdIntensity.getName(),stdIntensity);
        if (verbose) System.out.println("       Std intensity (sample) = "+stdIntensity.getValue());

        HCSingleMeasurement minIntensity = new HCSingleMeasurement(HCSingleMeasurement.MIN_INTENSITY,cs.getMin()[0]);
        minIntensity.setSource(this);
        inputImage.addMeasurement(minIntensity.getName(),minIntensity);
        if (verbose) System.out.println("       Min intensity = "+minIntensity.getValue());

        HCSingleMeasurement maxIntensity = new HCSingleMeasurement(HCSingleMeasurement.MAX_INTENSITY,cs.getMax()[0]);
        maxIntensity.setSource(this);
        inputImage.addMeasurement(maxIntensity.getName(),maxIntensity);
        if (verbose) System.out.println("       Max intensity = "+maxIntensity.getValue());

    }

    @Override
    public HCParameterCollection initialiseParameters() {
        HCParameterCollection parameters = new HCParameterCollection();

        parameters.addParameter(new HCParameter(this,MODULE_TITLE, HCParameter.MODULE_TITLE,"Measure image intensity",false));
        parameters.addParameter(new HCParameter(this,INPUT_IMAGE, HCParameter.INPUT_IMAGE,"Im1",false));

        return parameters;

    }

    @Override
    public HCParameterCollection getActiveParameters() {
        return parameters;
    }

}