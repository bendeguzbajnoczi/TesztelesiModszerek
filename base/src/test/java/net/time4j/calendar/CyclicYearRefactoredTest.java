package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class CyclicYearRefactoredTest {

    @Test
    public void equals() {
        assertThat(
            CyclicYear.of(29),
            is(CyclicYear.of(CyclicYear.Stem.REN_9_WATER_YANG, CyclicYear.Branch.CHEN_5_DRAGON)));
    }

    @Test
    public void nonEquals() {
        assertThat(
            CyclicYear.of(29).equals(SexagesimalName.of(29)),
            is(false));
    }

    @Test
    public void ofStemAndBranch() {
        for (int i = 1; i <= 60; i++) {
            CyclicYear cy = CyclicYear.of(i);
            assertThat(
                CyclicYear.of(cy.getStem(), cy.getBranch()),
                is(cy));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofStemAndBranchInvalid() {
        CyclicYear.of(SexagesimalName.Stem.GENG_7_METAL_YANG, SexagesimalName.Branch.CHOU_2_OX);
    }

    @Test
    public void getNumber() {
        assertThat(
            CyclicYear.of(29).getNumber(),
            is(29));
    }

    @Test
    public void getStem() {
        assertThat(
            CyclicYear.of(29).getStem(),
            is(CyclicYear.Stem.REN_9_WATER_YANG));
    }

    @Test
    public void getBranch() {
        assertThat(
            CyclicYear.of(29).getBranch(),
            is(CyclicYear.Branch.CHEN_5_DRAGON));
    }

    @Test
    public void getZodiac() {
        assertThat(
            CyclicYear.of(29).getZodiac(Locale.GERMAN),
            is("Drache"));
    }

    @Test
    public void getDisplayName() {
        class Case {
            final int year;
            final String expected;
            Case(int year, String expected) {
                this.year = year;
                this.expected = expected;
            }
        }

        Case[] cases = {
                new Case(1, "jiǎ-zǐ"),
                new Case(2, "yǐ-chǒu"),
                new Case(3, "bǐng-yín"),
                new Case(4, "dīng-mǎo"),
                new Case(5, "wù-chén"),
                new Case(6, "jǐ-sì"),
                new Case(7, "gēng-wǔ"),
                new Case(8, "xīn-wèi"),
                new Case(9, "rén-shēn"),
                new Case(10, "guǐ-yǒu"),
                new Case(11, "jiǎ-xū"),
                new Case(12, "yǐ-hài"),
                new Case(13, "bǐng-zǐ"),
                new Case(59, "rén-xū"),
                new Case(60, "guǐ-hài")
        };

        for (Case c : cases) {
            assertThat("Year " + c.year,
                    CyclicYear.of(c.year).getDisplayName(Locale.ENGLISH),
                    is(c.expected));
        }
    }


    @Test
    public void roll() {
        assertThat(
            CyclicYear.of(CyclicYear.Stem.YI_2_WOOD_YIN, CyclicYear.Branch.HAI_12_PIG).roll(5),
            is(CyclicYear.of(17)));
        assertThat(
            CyclicYear.of(59).roll(3),
            is(CyclicYear.of(2)));
        assertThat(
            CyclicYear.of(2).roll(-3),
            is(CyclicYear.of(59)));
    }

    @Test
    public void parse() throws ParseException {
        CyclicYear expected = CyclicYear.of(
                CyclicYear.Stem.YI_2_WOOD_YIN,
                CyclicYear.Branch.HAI_12_PIG
        );

        class ParseCase {
            final String input;
            final Locale locale;

            ParseCase(String input, Locale locale) {
                this.input = input;
                this.locale = locale;
            }
        }

        ParseCase[] cases = new ParseCase[] {
                new ParseCase("yi-hai", Locale.ROOT),
                new ParseCase("yǐ-hài", Locale.ROOT),
                new ParseCase("yi-hai", Locale.GERMAN),
                new ParseCase("yǐ-hài", Locale.GERMAN),
                new ParseCase("乙亥", Locale.CHINESE),
                new ParseCase("을해", Locale.KOREAN),
                new ParseCase("И-Хай", new Locale("ru"))
        };

        for (ParseCase pc : cases) {
            assertThat(
                    "Parsing '" + pc.input + "' with locale " + pc.locale,
                    CyclicYear.parse(pc.input, pc.locale),
                    is(expected)
            );
        }
    }

}