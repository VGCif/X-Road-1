package ee.ria.xroad.signer.protocol.dto;

import java.io.Serializable;

/**
 * Key usage can either be signing or authentication.
 */
public enum KeyUsageInfo implements Serializable {

    SIGNING,
    AUTHENTICATION;

}