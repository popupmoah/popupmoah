package com.sgyj.popupmoah;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JasyptTest {

    @Autowired
    private StringEncryptor jasyptStringEncryptor;

    @Test
    @DisplayName("암복호화 테스트")
    void encrypt () {
        // given
        String plainText = "Hello, Jasypt!";
        // when
        String encrypt = jasyptStringEncryptor.encrypt(plainText);
        String decrypt = jasyptStringEncryptor.decrypt(encrypt);
        // then
        assertThat(decrypt).isEqualTo(plainText);

    }

}
