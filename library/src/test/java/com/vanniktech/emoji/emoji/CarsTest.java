package com.vanniktech.emoji.emoji;

import org.junit.Test;


public class CarsTest {
    @Test
    public void constructorShouldBePrivate() {
        PrivateConstructorChecker.forClass(Cars.class).expectedTypeOfException(AssertionError.class).expectedExceptionMessage("No instances.").check();
    }
}
