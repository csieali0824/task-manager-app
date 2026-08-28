// [AI assisted 005]
package com.taskmanager.exception;

/** The request body is not a usable JSON Merge Patch document (RFC 7396). */
public class InvalidPatchException extends RuntimeException {

    public InvalidPatchException(String message) {
        super(message);
    }
}
