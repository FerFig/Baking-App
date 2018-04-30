package com.ferfig.bakingapp.model.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.ferfig.bakingapp.model.converter.DataTypeConverter;
import com.ferfig.bakingapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = Utils.DB_TABLE_RECIPES)
public class Recip implements Parcelable{
    @PrimaryKey
    private Integer id;
    private String name;
    @TypeConverters(DataTypeConverter.class)
    private List<Ingredient> ingredients = new ArrayList<>();
    @TypeConverters(DataTypeConverter.class)
    private List<Step> steps = new ArrayList<>();
    private Integer servings;
    private String image;
    public final static Parcelable.Creator<Recip> CREATOR = new Creator<Recip>() {

        public Recip createFromParcel(Parcel in) {
            return new Recip(in);
        }

        public Recip[] newArray(int size) {
            return (new Recip[size]);
        }

    };

    protected Recip(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.ingredients, (Ingredient.class.getClassLoader()));
        in.readList(this.steps, (Step.class.getClassLoader()));
        this.servings = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Recip() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeList(ingredients);
        dest.writeList(steps);
        dest.writeValue(servings);
        dest.writeValue(image);
    }

    public int describeContents() {
        return 0;
    }
}
