package com.ferfig.bakingapp.model.converter;

import android.arch.persistence.room.TypeConverter;

import com.ferfig.bakingapp.model.entity.Ingredient;
import com.ferfig.bakingapp.model.entity.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DataTypeConverter {
    private static Gson gson = new Gson();

    /*** Converter for Ingredients **/
    @TypeConverter
    public static List<Ingredient> stringToIngredientList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ingredient>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ingredientListToString(List<Ingredient> ingredients) {
        return gson.toJson(ingredients);
    }

    /*** Converter for Steps **/
    @TypeConverter
    public static List<Step> stringToStepList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Step>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String stepListToString(List<Step> steps) {
        return gson.toJson(steps);
    }
}
