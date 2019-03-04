package ru.maklas.genetics.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;
import ru.maklas.genetics.states.GenerationDistribution;

public class MainMenuView extends BaseStage {

    private final VisTextButton start;
    private final VisTextButton selectFunction;
    private final VisLabel functionDescription;
    private final VisValidatableTextField populationSizeField;
    private final VisValidatableTextField minField;
    private final VisValidatableTextField maxField;
    private final VisValidatableTextField generationMemoryField;
    private final VisValidatableTextField crossPointField;
    private final VisValidatableTextField bitMutMin;
    private final VisValidatableTextField bitMutMax;
    private final VisValidatableTextField bitsPerGeneField;
    private final VisValidatableTextField qField;
    private final VisSelectBox<GenerationDistribution> generationDistributionBox;
    private final VisLabel precisionLabel;

    public MainMenuView() {
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.align(Align.topLeft);
        table.pad(20);
        table.defaults().padBottom(10).padRight(5).left();
        addActor(table);

        selectFunction = new VisTextButton("Функция");
        functionDescription = label("");
        start = new VisTextButton("start");
        populationSizeField = new VisValidatableTextField(new Validators.IntegerValidator());
        minField = new VisValidatableTextField(new Validators.FloatValidator());
        maxField = new VisValidatableTextField(new Validators.FloatValidator());
        generationMemoryField = new VisValidatableTextField(new Validators.IntegerValidator());
        crossPointField = new VisValidatableTextField(new Validators.IntegerValidator());
        bitMutMin = new VisValidatableTextField(new Validators.IntegerValidator());
        bitMutMax = new VisValidatableTextField(new Validators.IntegerValidator());
        bitsPerGeneField = new VisValidatableTextField(new Validators.IntegerValidator());
        qField = new VisValidatableTextField(new Validators.FloatValidator());
        generationDistributionBox = new VisSelectBox<>();
        generationDistributionBox.setItems(GenerationDistribution.values());
        generationDistributionBox.setSelected(GenerationDistribution.EVEN);
        precisionLabel = label("");


        table.add(selectFunction).padRight(10);
        table.add(functionDescription).colspan(4);
        table.row();

        table.add(start);
        table.row();

        table.add(label("Gene length")).right();
        table.add(bitsPerGeneField).width(75);
        table.add(precisionLabel);
        table.row();

        table.add(label("Population size")).right();
        table.add(populationSizeField).width(75);
        table.row();

        table.add(label("Min-Max value")).right();
        table.add(minField).width(75);
        table.add(maxField).width(75).left();
        table.row();

        table.add(label("Generation destribution")).right();
        table.add(generationDistributionBox).width(75);
        table.row();

        table.add(label("Generation memory")).right();
        table.add(generationMemoryField).width(75);
        table.row();

        table.add(label("Cross points")).right();
        table.add(crossPointField).width(75);
        table.row();

        table.add(label("Bit mutations min-max")).right();
        table.add(bitMutMin).width(75);
        table.add(bitMutMax).width(75).left();
        table.row();

        table.add(label("Q")).right();
        table.add(qField).width(75);
        table.row();
    }
    
    private VisLabel label(String text){
        return new VisLabel(text, Color.BLACK);
    }

    public void onStart(Runnable r){
        start.addChangeListener(r);
    }

    public void onSelectFunction(Runnable r){
        selectFunction.addChangeListener(r);
    }

    public void setFunctionDescription(String s){
        functionDescription.setText(s);
    }

    public void setPopulationSize(int size){
        populationSizeField.setText(String.valueOf(size));
    }

    public int getPopulationSize(){
        return getInt(populationSizeField);
    }

    public void setMinMax(int min, int max){
        minField.setText(String.valueOf(min));
        maxField.setText(String.valueOf(max));
    }

    public float getMin(){
        return getFloat(minField);
    }

    public float getMax(){
        return getFloat(maxField);
    }

    public int getBitsPerGene(){
        return getInt(bitsPerGeneField);
    }

    public void setBitsPerGeneLen(int len){
        bitsPerGeneField.setText(String.valueOf(len));
    }

    public int getGenerationMemory(){
        return getInt(generationMemoryField);
    }

    public void setGenerationMemory(int val){
        generationMemoryField.setText(String.valueOf(val));
    }

    public int getCrossingPoints(){
        return getInt(crossPointField);
    }

    public void setCrossingPoints(int val){
        crossPointField.setText(String.valueOf(val));
    }

    public int getBitMutationMin(){
        return getInt(bitMutMin);
    }

    public int getBitMutationMax(){
        return getInt(bitMutMax);
    }

    public void setBitMutationMinMax(int min, int max){
        bitMutMin.setText(String.valueOf(min));
        bitMutMax.setText(String.valueOf(max));
    }

    public double getQ(){
        return getFloat(qField);
    }

    public void setQ(double q){
        qField.setText(String.valueOf(q));
    }

    public GenerationDistribution getGenerationDistribution(){
        return generationDistributionBox.getSelected();
    }

    public void setGenerationDistribution(GenerationDistribution distribution){
        generationDistributionBox.setSelected(distribution);
    }

    private static int getInt(VisTextField field){
        try {
            return Integer.parseInt(field.getText().replaceAll("[^\\d-]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static float getFloat(VisTextField field){
        try {
            return Float.parseFloat(field.getText().replaceAll("[^\\d-.]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setPrecision(String s){
        precisionLabel.setText("Precision: " + s);
    }

}
