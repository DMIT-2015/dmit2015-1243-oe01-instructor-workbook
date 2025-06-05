package dmit2015.service;

import dmit2015.model.LibraryAccount;

import java.util.List;
import java.util.Optional;

public interface LibraryAccountService {

    LibraryAccount createLibraryAccount(LibraryAccount libraryAccount);

    Optional<LibraryAccount> getLibraryAccountById(String id);

    List<LibraryAccount> getAllLibraryAccounts();

    LibraryAccount updateLibraryAccount(LibraryAccount libraryAccount);

    void deleteLibraryAccountById(String id);
}