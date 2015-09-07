/**
 * 
 */
package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.dto.TransactionTypeDto;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.exception.TransactionTypeHasTransactionsException;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface FinancialTransactionService {

  /**
   * Find all transaction types
   * 
   * @param page the page requested
   * @param size the size
   * @return the transaction types
   */
  Collection<TransactionTypeDto> findAllTransactionTypes(int page, int size);

  /**
   * Find transaction type by given id
   * 
   * @param id the id of the transaction type
   * @return the transaction type
   * @throws ResourceNotFoundException if the transaction can't be found
   */
  TransactionTypeDto findTransactionTypeById(Long id);

  /**
   * Add a new transaction type
   * 
   * @param transactionType the transaction type to be added
   * @return the transaction type saved
   * @throws ResourceAlreadyExistsException if the transaction was previously saved
   */
  TransactionTypeDto addTransactionType(TransactionTypeDto transactionType);

  /**
   * Update transaction type
   * 
   * @param transactionType to be updated
   * @return the transaction type updated
   * @throws ResourceNotFoundException if the traansaction can't be found
   */
  TransactionTypeDto updateTransactionType(TransactionTypeDto transactionType);


  /**
   * Delete a transaction type
   * 
   * @param id of the transaction type;
   * @throws ResourceNotFoundException if the transaction can't be found
   * @throws TransactionTypeHasTransactionsException if transactions uses this category
   */
  void deleteTransactionType(Long id);


  /**
   * Find a transaction with the given id
   * 
   * @param id of the transaction to search
   * @return the transaction
   * @throws ResourceNotFoundException if the Transaction can't be found
   */
  FinancialTransactionDto

  findTransactionById(Long id);

  /**
   * Find all transactions that belongs to an invoice number
   * 
   * @param number of the invoice
   * @param page the page requested
   * @param size the size of the page
   * @return the transactions
   * @throws ResourceNotFoundException if the invoice doesn't exists
   */
  Collection<FinancialTransactionDto> findTransactionsByInvoiceNumber(Long number, int page, int size);

  /**
   * Pay an invoice
   * 
   * @param transaction
   * @return the transaction saved
   * @throws ResourceAlreadyExistsException if the transaction exists
   */
  FinancialTransactionDto payInvoice(FinancialTransactionDto transaction);

  /**
   * Update transaction
   * 
   * @param transaction the transaction to be updated
   * @return the transaction updated
   * @throws ResourceNotFoundException if the transaction doesn't exists
   */
  FinancialTransactionDto updateFinancialTransaction(FinancialTransactionDto transaction);

  /**
   * Delete a transaction
   * 
   * @param id of the transaction
   * @throws ResourceNotFoundException if the transaction can't be found
   */
  void deleteFinancialTransaction(Long id);

}
