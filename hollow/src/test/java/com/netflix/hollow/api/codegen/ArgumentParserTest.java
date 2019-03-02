/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.hollow.api.codegen;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Test;

public class ArgumentParserTest {
    enum Commands {
        orderCoffee,
        drinkCoffee,
        writeArgumentParserClass;
    };

    @Test(expected = IllegalArgumentException.class)
    public void test_missingDash() {
        new ArgumentParser<Commands>(Commands.class, new String[]{"-orderCoffee=f"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_missingValue() {
        new ArgumentParser<Commands>(Commands.class, new String[]{"--orderCoffee"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_missingArgument() {
        new ArgumentParser<Commands>(Commands.class, new String[]{"--compileOnFirstTry=f"});
    }

    @Test
    public void test_noCommands() {
        assertEquals(Arrays.asList(), new ArgumentParser<Commands>(Commands.class, new String[] {}).getParsedArguments());
    }

    @Test
    public void test_validCommands() {
        assertEquals(3, new ArgumentParser<Commands>(Commands.class, new String[] {
            "--orderCoffee=first", "--drinkCoffee=next", "--writeArgumentParserClass=yay"}).getParsedArguments().size());
        assertParsedArgument("--orderCoffee=fi.rst", Commands.orderCoffee, "fi.rst");
        assertParsedArgument("--drinkCoffee=ne/x--t", Commands.drinkCoffee, "ne/x--t");
        assertParsedArgument("--writeArgumentParserClass=compl_ete", Commands.writeArgumentParserClass, "compl_ete");

    }

    private void assertParsedArgument(String commandLineArg, Commands key, String value) {
        ArgumentParser<Commands>.ParsedArgument parsed = new ArgumentParser<>(Commands.class, new String[] {
            commandLineArg}).getParsedArguments().get(0);
        assertEquals(key, parsed.getKey());
        assertEquals(value, parsed.getValue());
    }
}
