# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

**Key Challenges:**

- **Shared resource attribution:** A single warehouse may serve multiple stores. Overhead costs like building lease, utilities, and management headcount must be fairly split — typically by throughput volume or square footage allocation — rather than treated as indivisible lump sums.
- **Labor cost granularity:** Warehouse labor often spans multiple functions (receiving, picking, packing, dispatch). Without time-tracking at the function level, you can't attribute labor cost to specific SKU flows or downstream stores.
- **Transportation cost routing:** Inbound freight is relatively straightforward to allocate per shipment. Last-mile delivery to stores is harder — routes serve multiple stores per run, and cost-per-stop models require clean trip data.
- **Inventory holding costs:** Carrying cost (capital tied up in stock × time × cost of capital) is often omitted in operational reporting. Including it accurately requires knowing average dwell time per SKU per warehouse, which demands event-level inventory data.
- **Period mismatches:** Invoices often arrive after cost events. Accrual discipline is essential to match costs to the period they were incurred, especially for transport and seasonal labor.

**Important Considerations:**

- **Cost center hierarchy design:** Define a clear hierarchy — Business Unit → Warehouse → Store → Product Category — before any system is built. Retrofitting cost center structures onto an existing chart of accounts is expensive.
- **Activity-Based Costing vs. traditional absorption:** ABC is more accurate but operationally heavy. For most fulfillment environments, a hybrid works well: ABC for high-variability cost drivers (labor, transport) and absorption for stable overhead (lease, utilities).
- **Real-time vs. batch tracking:** Real-time tracking adds system complexity but enables faster corrective action. Batch (daily/weekly) is sufficient for most cost types except labor, where shift-level data is preferable.
- **Data ownership and source-of-truth:** Costs often originate in WMS, TMS, HRMS, and ERP separately. Establishing which system owns each cost type prevents double-counting and reconciliation loops.

**Questions I Would Raise:**

- What is the current cost allocation methodology — is anything already being tracked at cost center level, or is everything at entity level? Understanding the baseline prevents rebuilding what already works.
- Are there warehouses that serve more than one business unit simultaneously? If so, shared-cost allocation rules need to be agreed on by finance before any technical implementation, since different rules produce materially different P&L outcomes per unit.
- What is the reporting cadence and audience — operational managers or finance controllers? The required granularity and latency differ significantly between the two.
- Is there existing labor time-tracking infrastructure, or is that a gap? If it's a gap, that's likely the most time-consuming item to address and should be scoped separately.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

**Potential Strategies:**

- **Demand-driven labor scheduling:** Shift from fixed headcount to flex-roster models aligned with order volume forecasts. This alone can reduce direct labor cost by 12–18% in seasonal operations, since warehouse staffing often lags demand changes by one to two weeks under fixed schedules.
- **Inventory positioning (slotting optimization):** High-velocity SKUs should be placed closest to dispatch points. Poor slotting forces pickers to travel further per pick, adding invisible labor cost at scale. Slotting reviews every quarter typically pay back within the first month of implementation.
- **Shared transport consolidation:** If multiple stores in a proximity cluster are served by separate runs, consolidating routes reduces per-delivery cost. Route consolidation requires clean store delivery-window data — a relatively cheap input with a large payback.
- **SKU rationalization at the warehouse level:** Long-tail SKUs with low turn rates consume disproportionate space and handling time. Identifying slow-movers and either discontinuing or centralizing them in fewer locations reduces complexity cost.
- **Vendor lead-time renegotiation:** Shorter lead times allow lower safety stock levels, reducing holding cost. The inventory cost saving is directly visible on the fulfillment P&L.

**How I Would Identify and Prioritize:**

- **Identify via cost driver analysis:** Start with a Pareto of cost lines — which 20% of cost categories account for 80% of spend. Then decompose each into controllable and non-controllable components. Only controllable costs are candidates for optimization.
- **Prioritize by impact × feasibility matrix:** Score each initiative on expected savings magnitude, time-to-realize, and operational disruption risk. Quick wins (high impact, low disruption) go first to build stakeholder confidence. Structural changes (slotting, route redesign) are sequenced after baseline data is solid.
- **Implement with baseline / target / actual tracking:** Each initiative should have a baseline cost metric, a target, and a measurement period. Without this, cost savings disappear into general variance and initiatives cannot be evaluated independently.

**Questions I Would Raise:**

- What service level agreements exist with stores? Any cost optimization that risks SLA breaches will be rejected operationally — the boundary conditions need to be understood before options are evaluated.
- Are there existing benchmarks against industry peers or internal targets? Knowing whether current cost per order is above or below sector norms helps set realistic optimization targets and avoid over-engineering.
- Who owns the optimization backlog — finance, operations, or a shared function? Without clear ownership, initiatives stall between teams after identification.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

**Why This Integration Matters:**

- **Single source of truth:** Without integration, operational and financial data diverge — teams work from different numbers, reconciliation takes time that could be spent on analysis, and decisions are made on stale figures.
- **Audit and compliance:** Financial systems are the ledger of record. Cost data that lives only in an operational tool has no audit trail that satisfies a finance or external audit requirement. Integration pushes confirmed data into the authoritative record.
- **Faster period close:** Month-end close is typically delayed by manual data gathering from operational systems. A well-integrated Cost Control Tool can pre-populate journals, accruals, and cost allocations, compressing the close cycle.

**Ensuring Seamless Integration:**

- **Event-driven architecture over batch:** An event-driven approach (e.g., domain events published on cost transactions) rather than nightly batch exports allows financial systems to consume changes within seconds of confirmation, which matters for intra-day operational dashboards.
- **Idempotent message handling:** Financial integrations must handle duplicates gracefully. Using stable event IDs and idempotent consumers prevents double-posting to the ledger — a critical correctness requirement in financial contexts.
- **Data mapping as a governed artefact:** The mapping between operational cost categories and financial chart-of-accounts codes should be version-controlled and owned by finance, not embedded in application code. This prevents mapping drift when the COA changes.
- **Reconciliation endpoint:** Provide a daily reconciliation report comparing cost totals across systems. Discrepancies are flagged and routed for investigation before they compound across periods.
- **Rollback and correction handling:** The integration must support correction events that produce compensating entries in the financial system, rather than requiring manual journal fixes.

**Questions I Would Raise:**

- What financial system is in use — ERP (SAP, Oracle, NetSuite)? Each has different integration patterns and certification requirements. Some require certified connectors; building custom integrations can introduce liability in regulated environments.
- What is the latency tolerance for cost data in the financial system — near-real-time or end-of-day is sufficient? This determines whether a streaming or batch integration pattern is appropriate and significantly affects implementation cost.
- Are there multi-currency or multi-entity requirements? Cross-currency cost consolidation introduces FX translation complexity that should be scoped early, as it affects both data model and integration design.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

**Why Budgeting & Forecasting Is Essential:**

- **Resource allocation discipline:** Without a forward-looking cost view, operational decisions — headcount, equipment, space — are made reactively. A forecast translates volume plans into cost expectations, allowing proactive resource staging.
- **KPI alignment:** Budgets define the performance baseline against which operational efficiency is measured. A cost-per-unit budget gives warehouse managers a target, not just a historical average to compare against.
- **Capital planning:** Warehouse infrastructure investments (automation, WMS upgrades) need multi-year cost models to justify ROI. Without forecasting, these decisions default to intuition rather than data.

**Key Design Considerations for the System:**

- **Driver-based budgeting:** Rather than extrapolating last year's costs, build forecasts from operational drivers — expected order volumes, SKU mix, planned headcount. This produces budgets that flex correctly when volume changes, rather than becoming obsolete the moment the plan diverges from reality.
- **Seasonality modelling:** Fulfillment costs are rarely linear. Peak trading periods can double labor and transport costs. The model must capture seasonal patterns from at least two prior years and allow planners to adjust for known changes (new product launches, market expansion).
- **Rolling 12-month forecast:** Annual budgets become stale by Q3. A rolling forecast — updated monthly with the latest actuals — gives management a current view of the year-end position and avoids the annual budget cycle becoming a political rather than analytical exercise.
- **Scenario planning capability:** Build three scenarios at minimum: base case, downside (volume shortfall), and upside (volume spike). Each should produce a distinct cost and resource requirement profile, so leadership can pre-authorize contingency responses rather than scrambling when a scenario materializes.
- **Variance analysis at the cost driver level:** Variance should decompose into volume variance (we did more/less than planned) and efficiency variance (we spent more/less per unit than planned). These require different management responses.

**Questions I Would Raise:**

- What is the current budgeting process — top-down, bottom-up, or negotiated? This determines how the tool needs to support input collection from warehouse managers versus finance-driven allocation.
- How far in advance does the business plan? A 90-day horizon has very different data and model requirements from an 18-month strategic plan. Both are valid but should be treated as separate views with appropriate confidence bands.
- Is there appetite for machine-learning-assisted forecasting, or is a transparent formula-based model preferred? ML can improve accuracy but reduces interpretability — in finance contexts, explainability often matters more than marginal accuracy gains.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

**Why Preserving Cost History Is Critical:**

- **Audit and regulatory compliance:** Financial history for a business unit may need to be accessible for 5–7 years under standard accounting regulations. Archiving the old warehouse entity preserves this without contaminating the new entity's performance record.
- **Performance benchmarking:** The new warehouse needs a clean cost baseline from day one. If old costs are merged into the new entity's record, KPI comparisons become meaningless — managers can't tell whether a cost increase reflects the new operation's performance or is a legacy carry-over.
- **Budget continuity:** The budget for the new warehouse may be derived from the old warehouse's actuals. If historical data is corrupted or inaccessible, budget accuracy degrades. The archive is not just a compliance record — it's an input to the forward plan.
- **Cost basis of transferred stock:** When stock moves from the old to the new warehouse, its cost basis (purchase price + carrying cost to date) must transfer correctly. Mishandling this creates phantom P&L variance in the first reporting period of the new entity.

**Keeping the New Warehouse Within Budget:**

- **Replacement cost modelling:** Before the replacement, model the expected cost delta between old and new — new lease terms, different location logistics costs, ramp-up labor. This becomes the amended budget baseline, not a copy of the old warehouse's budget.
- **Ramp-up cost tracking:** New warehouses incur one-time costs (fit-out, staff training, WMS configuration) that should be tagged separately from ongoing operating costs. Without this, the ramp period inflates the cost-per-unit metric and creates misleading comparisons.
- **BU Code transition governance:** The moment the Business Unit Code is reassigned, all new cost transactions must post to the new entity. A clear cutover date, communicated to all feeder systems simultaneously, prevents costs from being misrouted to the archived entity or vice versa.
- **Archival verification:** Before the old entity is marked inactive, a reconciliation should confirm that all outstanding purchase orders, accruals, and liabilities are resolved or formally transferred. An unclosed liability on an archived entity is difficult to process and creates downstream audit findings.

**Questions I Would Raise:**

- How will the Cost Control Tool distinguish between the old and new warehouse when the BU code is shared? The system must use a surrogate key (internal ID + effective date) to maintain separate cost histories under the same business-facing code — otherwise reporting queries will inadvertently blend historical and current costs.
- What is the stock transfer mechanism — a physical stock count or a book transfer? The approach affects both the timing and the cost accounting treatment of the transferred inventory, and the two methods reconcile differently against the financial ledger.
- Is there a parallel-run period where both warehouses operate simultaneously? If so, shared costs during the overlap need explicit allocation rules defined in advance, or the period-end close becomes an unresolvable reconciliation problem.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
