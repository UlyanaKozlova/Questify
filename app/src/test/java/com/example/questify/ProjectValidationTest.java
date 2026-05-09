package com.example.questify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.example.questify.domain.model.Project;
import com.example.questify.util.exception.DomainValidationException;

import org.junit.Test;


public class ProjectValidationTest {

    private static String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }

    @Test
    public void name_empty_throws() {
        try {
            new Project("");
            fail("expected DomainValidationException");
        } catch (DomainValidationException e) {
            assertEquals(R.string.error_project_name_short, e.resId);
        }
    }

    @Test
    public void name_twoChars_throws() {
        try {
            new Project("ab");
            fail("expected DomainValidationException");
        } catch (DomainValidationException e) {
            assertEquals(R.string.error_project_name_short, e.resId);
        }
    }

    @Test
    public void name_threeChars_passes() {
        Project p = new Project("abc");
        assertEquals("abc", p.getProjectName());
    }

    @Test
    public void name_longString_passes() {
        String name = repeat('y', 80);
        Project p = new Project(name);
        assertEquals(name, p.getProjectName());
    }
}
