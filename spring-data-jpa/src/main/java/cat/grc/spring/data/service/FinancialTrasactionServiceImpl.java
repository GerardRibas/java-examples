/**
 * 
 */
package cat.grc.spring.data.service;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.dto.TransactionTypeDto;
import cat.grc.spring.data.entity.FinancialTransaction;
import cat.grc.spring.data.entity.TransactionType;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.exception.TransactionTypeHasTransactionsException;
import cat.grc.spring.data.repository.FinancialTransactionRepository;
import cat.grc.spring.data.repository.TransactionTypeRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Service
public class FinancialTrasactionServiceImpl implements FinancialTransactionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceServiceImpl.class);

  private FinancialTransactionRepository financialTransactionRepository;

  private TransactionTypeRepository transactionTypeRepository;

  private InvoiceService invoiceService;

  private ModelMapper modelMapper;

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.InvoiceService#findTransactionById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public FinancialTransactionDto findTransactionById(Long id) {
    LOGGER.debug("Find transaction by id={}", id);
    FinancialTransaction transaction = financialTransactionRepository.findOne(id);
    if (transaction == null) {
      String msg = String.format("Transaction does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    return modelMapper.map(transaction, FinancialTransactionDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.InvoiceService#findTransactionsByInvoiceNumber(java.lang.Long,
   * int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<FinancialTransactionDto> findTransactionsByInvoiceNumber(Long number, int page, int size) {
    LOGGER.debug("Find transaction by InvoiceNumber={}", number);
    invoiceService.findInvoiceById(number);
    Pageable pageable = new PageRequest(page, size);
    Page<FinancialTransaction> transactionsPage = financialTransactionRepository.findByInvoice(number, pageable);
    return transactionsPage.getContent().stream()
        .map(transaction -> modelMapper.map(transaction, FinancialTransactionDto.class)).collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.InvoiceService#payInvoice(cat.grc.spring.data.dto.
   * FinancialTransactionDto)
   */
  @Override
  @Transactional
  public FinancialTransactionDto payInvoice(FinancialTransactionDto transaction) {
    LOGGER.debug("Paying transaction={}", transaction);
    Assert.notNull(transaction);
    Assert.notNull(transaction.getAccountId());
    Assert.notNull(transaction.getTypeCode());
    invoiceService.findInvoiceById(transaction.getInvoiceNumber());
    boolean exists = transaction.getId() == null ? false : financialTransactionRepository.exists(transaction.getId());
    if (exists) {
      String msg = String.format("Transaction=%s already exists", transaction.getId());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    FinancialTransaction savedTransaction =
        financialTransactionRepository.save(modelMapper.map(transaction, FinancialTransaction.class));
    return modelMapper.map(savedTransaction, FinancialTransactionDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.InvoiceService#deleteFinancialTransaction(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteFinancialTransaction(Long id) {
    LOGGER.debug("Deleting transaction by id {}", id);
    transactionMustExists(id);
    financialTransactionRepository.delete(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.FinancialTransactionService#findAllTransactionTypes(int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<TransactionTypeDto> findAllTransactionTypes(int page, int size) {
    LOGGER.debug("Find all transaction types");
    Pageable pageable = new PageRequest(page, size);
    Page<TransactionType> transactionsPage = transactionTypeRepository.findAll(pageable);
    return transactionsPage.getContent().stream()
        .map(transaction -> modelMapper.map(transaction, TransactionTypeDto.class)).collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.FinancialTransactionService#findTransactionTypeById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public TransactionTypeDto findTransactionTypeById(Long id) {
    return modelMapper.map(findTransactionTypeEntityById(id), TransactionTypeDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.FinancialTransactionService#addTransactionType(cat.grc.spring.data.
   * dto.TransactionTypeDto)
   */
  @Override
  @Transactional
  public TransactionTypeDto addTransactionType(TransactionTypeDto transactionType) {
    LOGGER.debug("Adding new transaction type {}", transactionType);
    Assert.notNull(transactionType);
    boolean exists =
        transactionType.getCode() == null ? false : transactionTypeRepository.exists(transactionType.getCode());
    if (exists) {
      String msg = String.format("TransactionType=%s already exists", transactionType.getCode());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    TransactionType entity = transactionTypeRepository.save(modelMapper.map(transactionType, TransactionType.class));
    return modelMapper.map(entity, TransactionTypeDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.FinancialTransactionService#updateTransactionType(cat.grc.spring.
   * data.dto.TransactionTypeDto)
   */
  @Override
  @Transactional
  public TransactionTypeDto updateTransactionType(TransactionTypeDto transactionType) {
    LOGGER.debug("Updating transaction type {}", transactionType);
    Assert.notNull(transactionType);
    transactionTypeMustExists(transactionType.getCode());
    TransactionType entity = transactionTypeRepository.save(modelMapper.map(transactionType, TransactionType.class));
    return modelMapper.map(entity, TransactionTypeDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.FinancialTransactionService#deleteTransactionType(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteTransactionType(Long id) {
    LOGGER.debug("Deleting transaction type by id={}", id);
    TransactionType transactionType = findTransactionTypeEntityById(id);
    if (!transactionType.getTransactions().isEmpty()) {
      String msg = String.format("Transaction type %d has transactions associated", id);
      LOGGER.error(msg);
      throw new TransactionTypeHasTransactionsException(msg);
    }
    transactionTypeRepository.delete(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.FinancialTransactionService#updateFinancialTransaction(cat.grc.
   * spring.data.dto.FinancialTransactionDto)
   */
  @Override
  @Transactional
  public FinancialTransactionDto updateFinancialTransaction(FinancialTransactionDto transaction) {
    LOGGER.debug("Updating financial transaction {}", transaction);
    Assert.notNull(transaction);
    transactionMustExists(transaction.getId());
    FinancialTransaction entity =
        financialTransactionRepository.save(modelMapper.map(transaction, FinancialTransaction.class));
    return modelMapper.map(entity, FinancialTransactionDto.class);
  }

  @Resource
  public void setFinancialTransactionRepository(FinancialTransactionRepository financialTransactionRepository) {
    this.financialTransactionRepository = financialTransactionRepository;
  }

  @Resource
  public void setTransactionTypeRepository(TransactionTypeRepository transactionTypeRepository) {
    this.transactionTypeRepository = transactionTypeRepository;
  }

  @Resource
  public void setInvoiceService(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @Resource
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  private TransactionType findTransactionTypeEntityById(Long id) {
    LOGGER.debug("Find transaction type by id={}", id);
    TransactionType transaction = transactionTypeRepository.findOne(id);
    if (transaction == null) {
      String msg = String.format("Transaction type does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    return transaction;
  }

  private boolean transactionTypeMustExists(Long id) {
    Assert.notNull(id);
    boolean result = transactionTypeRepository.exists(id);
    if (!result) {
      String msg = String.format("CategoryCode=%s not found", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return result;
  }

  private boolean transactionMustExists(Long id) {
    Assert.notNull(id);
    boolean exists = financialTransactionRepository.exists(id);
    if (!exists) {
      String msg = String.format("Transaction does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    return exists;
  }

}
