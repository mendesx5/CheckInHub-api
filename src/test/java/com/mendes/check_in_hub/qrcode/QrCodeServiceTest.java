package com.mendes.check_in_hub.qrcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeServiceTest {

    private final QrCodeService qrCodeService =
            new QrCodeService();

    @Test
    void shouldGenerateQrCode() throws Exception {
        byte[] qrCode =
                qrCodeService.generateQrCode(
                        "test-token"
                );

        assertNotNull(qrCode);
        assertTrue(qrCode.length > 0);
    }

}
