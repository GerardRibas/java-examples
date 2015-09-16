/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.data.service.InvoiceService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RestController
public class InvoiceController {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

  private InvoiceService service;

  @RequestMapping("/customers/{customerId}/invoices")
  public Collection<InvoiceDto> getInvoicesByCustomer(@PathVariable Long customerId,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding invoices for customerId={} page={}, size={}", customerId, page, size);
    return service.findInvoicesByCustomer(customerId, page, size);
  }

  @RequestMapping(value = {"/customers/{customerId}/invoices", "/invoices"}, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public InvoiceDto createInvoice(@RequestBody InvoiceDto invoice) {
    LOGGER.debug("Creating invoice={}", invoice);
    return service.createInvoice(invoice);
  }

  @RequestMapping(value = {"/customers/{customerId}/invoices/{id}", "/invoices/{id}"})
  public InvoiceDto findInvoice(@PathVariable Long id) {
    LOGGER.debug("Finding invoice by id={}", id);
    return service.findInvoiceById(id);
  }

  @RequestMapping(value = {"/customers/{customerId}/invoices/{id}", "/invoices/{id}"}, method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteInvoice(@PathVariable Long id) {
    LOGGER.debug("Finding invoice by id={}", id);
    service.deleteInvoice(id);
  }

  @RequestMapping(value = {"/customers/{customerId}/invoices/{id}/lines", "/invoices/{id}/lines"})
  public Collection<InvoiceLineItemDto> findInvoiceLineItems(@PathVariable Long id,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding invoice line items for InvoicId={}", id);
    return service.findInvoiceLineItemsOfInvoice(id, page, size);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{orderId}/invoices", "/orders/{orderId}/invoices"})
  public Collection<InvoiceDto> getInvoicesByOrder(@PathVariable Long orderId,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding invoices for orderId={} page={}, size={}", orderId, page, size);
    return service.findInvoicesByOrder(orderId, page, size);
  }

  @Resource
  public void setService(InvoiceService service) {
    this.service = service;
  }

}
