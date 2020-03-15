package com.josephbleau.StravaMattermostConnector.service.registration;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VerificationCodeManager {
    private int length;
    private char[] characters;
    private String code;

    public VerificationCodeManager() {
        this.length = 4;
        this.characters = new char[]{'S','T','R','V','A', '1', '2','3','4','5'};
        this.code = generateCode(this.length, this.characters);
    }

    /**
     * Generate a random four digit code.
     */
    protected String generateCode(int length, char[] characters) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; ++i) {
            code.append(characters[random.nextInt(characters.length)]);
        }

        return code.toString();
    }

    public boolean verify(String codeToTest) {
        return this.code.equals(codeToTest);
    }

    public String getCode() {
        return this.code;
    }
}
