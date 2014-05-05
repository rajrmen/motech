package org.motechproject.hub.model.hibernate;

// Generated Apr 21, 2014 1:51:45 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * HubSubscriptionStatus generated by hbm2java
 */
@Entity
@Table(name = "hub_subscription_status", schema = "hub")
public class HubSubscriptionStatus implements java.io.Serializable {

	private static final long serialVersionUID = 8995781005450591068L;
	
	private long subscriptionStatusId;
	private String subscriptionStatusCode;
	private Set<HubSubscription> hubSubscriptions = new HashSet<HubSubscription>(
			0);

	public HubSubscriptionStatus() {
	}

	public HubSubscriptionStatus(long subscriptionStatusId,
			String subscriptionStatusCode) {
		this.subscriptionStatusId = subscriptionStatusId;
		this.subscriptionStatusCode = subscriptionStatusCode;
	}

	public HubSubscriptionStatus(long subscriptionStatusId,
			String subscriptionStatusCode, Set<HubSubscription> hubSubscriptions) {
		this.subscriptionStatusId = subscriptionStatusId;
		this.subscriptionStatusCode = subscriptionStatusCode;
		this.hubSubscriptions = hubSubscriptions;
	}

	@Id
	@Column(name = "subscription_status_id", unique = true, nullable = false)
	public long getSubscriptionStatusId() {
		return this.subscriptionStatusId;
	}

	public void setSubscriptionStatusId(long subscriptionStatusId) {
		this.subscriptionStatusId = subscriptionStatusId;
	}

	@Column(name = "subscription_status_code", nullable = false, length = 15)
	public String getSubscriptionStatusCode() {
		return this.subscriptionStatusCode;
	}

	public void setSubscriptionStatusCode(String subscriptionStatusCode) {
		this.subscriptionStatusCode = subscriptionStatusCode;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hubSubscriptionStatus")
	public Set<HubSubscription> getHubSubscriptions() {
		return this.hubSubscriptions;
	}

	public void setHubSubscriptions(Set<HubSubscription> hubSubscriptions) {
		this.hubSubscriptions = hubSubscriptions;
	}

}