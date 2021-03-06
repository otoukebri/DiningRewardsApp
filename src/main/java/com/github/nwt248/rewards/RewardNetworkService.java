package com.github.nwt248.rewards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.nwt248.common.MonetaryAmount;

/**
 * Rewards an Account for Dining at a Restaurant.
 * 
 * The sole Reward Network implementation. This object is an application-layer service responsible for coordinating with
 * the domain-layer to carry out the process of rewarding benefits to accounts for dining.
 * 
 * Said in other words, this class implements the "reward account for dining" use case.
 */

/* xTODO-03: Annotate the class with an appropriate stereotype annotation 
 * to cause component-scan to detect and load this bean.
 * Configure Dependency Injection for all 3 dependencies.  
 * Decide if you should use field level or constructor injection. */

@Service
public class RewardNetworkService implements RewardNetwork {

  private AccountRepository accountRepository;

  private RestaurantRepository restaurantRepository;

  private RewardRepository rewardRepository;

  /**
   * Creates a new reward network.
   * @param accountRepository the repository for loading accounts to reward
   * @param restaurantRepository the repository for loading restaurants that determine how much to reward
   * @param rewardRepository the repository for recording a record of successful reward transactions
   */
  @Autowired
  public RewardNetworkService(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
      RewardRepository rewardRepository) {
    this.accountRepository = accountRepository;
    this.restaurantRepository = restaurantRepository;
    this.rewardRepository = rewardRepository;
  }

  public RewardConfirmation rewardAccountFor(DiningEvent event) {
    Account account = accountRepository.findByCreditCard(event.getCreditCardNumber());
    Restaurant restaurant = restaurantRepository.findByMerchantNumber(event.getMerchantNumber());
    MonetaryAmount amount = restaurant.calculateBenefitFor(account, event);
    AccountContribution contribution = account.makeContribution(amount);
    accountRepository.updateBeneficiaries(account);
    return rewardRepository.confirmReward(contribution, event);
  }
}