package com.example.data.model

enum class HscGroup(val displayName: String, val description: String) {
    SCIENCE("Science", "Physics, Chemistry, Higher Math, Biology"),
    BUSINESS_STUDIES("Business Studies", "Accounting, Management, Finance, Marketing"),
    HUMANITIES("Humanities", "Economics, Civics, Logic, History, Geography")
}

data class SubjectInfo(
    val id: String,
    val name: String,
    val group: String, // "COMPULSORY", "SCIENCE", "BUSINESS_STUDIES", "HUMANITIES"
    val papers: List<String> = listOf("1st Paper", "2nd Paper"),
    val defaultChapters: List<String>
)

object HscSubjectCatalog {
    // Compulsory Subjects
    val compulsorySubjects = listOf(
        SubjectInfo(
            id = "bangla_1",
            name = "Bangla — 1st Paper",
            group = "COMPULSORY",
            papers = listOf("1st Paper"),
            defaultChapters = listOf(
                "Shobdo o Pod", "Aparichita", "Bilashi", "Amar Path",
                "Manob Kalyan", "Masipishi", "Bivishoner Proti Meghnad",
                "Sonar Tori", "Bidrohi", "Protidan", "Tahare-i Pore Mone",
                "Ei Purano Dhaka", "Lalshalu (Novel)", "Sirajuddaula (Drama)"
            )
        ),
        SubjectInfo(
            id = "bangla_2",
            name = "Bangla — 2nd Paper",
            group = "COMPULSORY",
            papers = listOf("2nd Paper"),
            defaultChapters = listOf(
                "Bangla Uchcharon", "Bangla Banan", "Bakko Totto",
                "Byakoronik Shobdosreni", "Shobdo Gothon", "Paribhashik Shobdo",
                "Onubad", "Dinlipi / Protibedon", "Bhabshomprosharon",
                "Rochona / Essay"
            )
        ),
        SubjectInfo(
            id = "english_1",
            name = "English — 1st Paper",
            group = "COMPULSORY",
            papers = listOf("1st Paper"),
            defaultChapters = listOf(
                "Unit 1: People or Personalities",
                "Unit 2: Dreams",
                "Unit 3: Lifestyle & Food Habits",
                "Unit 4: Youthful Encounters",
                "Unit 5: Relationships",
                "Unit 6: Adolescence",
                "Unit 7: Environment & Nature",
                "Unit 8: Art & Music",
                "Flow Chart & Summary Writing",
                "Theme & Story Writing"
            )
        ),
        SubjectInfo(
            id = "english_2",
            name = "English — 2nd Paper",
            group = "COMPULSORY",
            papers = listOf("2nd Paper"),
            defaultChapters = listOf(
                "Gap filling with prepositions",
                "Special phrases / words",
                "Completing sentences",
                "Right forms of verbs",
                "Narrative style (Speech)",
                "Modifiers",
                "Sentence connectors",
                "Synonyms and Antonyms",
                "Punctuation & Capitalization",
                "Formal Letter / Application / Paragraph"
            )
        ),
        SubjectInfo(
            id = "ict",
            name = "Information and Communication Technology (ICT)",
            group = "COMPULSORY",
            papers = listOf("Compulsory Single Paper"),
            defaultChapters = listOf(
                "Chapter 1: ICT in World & Bangladesh Perspective",
                "Chapter 2: Communication Systems & Networking",
                "Chapter 3: Number Systems & Digital Logic Device",
                "Chapter 4: Web Design & HTML",
                "Chapter 5: Programming Language (C Programming)",
                "Chapter 6: Database Management System (DBMS)"
            )
        )
    )

    // Science Subjects
    val scienceSubjects = listOf(
        SubjectInfo(
            id = "physics_1",
            name = "Physics — 1st Paper",
            group = "SCIENCE",
            papers = listOf("1st Paper"),
            defaultChapters = listOf(
                "Chapter 1: Physical World & Measurement",
                "Chapter 2: Vectors",
                "Chapter 3: Dynamics (Kinematics & Newton's Laws)",
                "Chapter 4: Work, Energy & Power",
                "Chapter 5: Circular Motion & Gravitation",
                "Chapter 6: Structural Properties of Matter",
                "Chapter 7: Periodic Motion",
                "Chapter 8: Waves",
                "Chapter 9: Ideal Gas and Kinetic Theory of Gases"
            )
        ),
        SubjectInfo(
            id = "physics_2",
            name = "Physics — 2nd Paper",
            group = "SCIENCE",
            papers = listOf("2nd Paper"),
            defaultChapters = listOf(
                "Chapter 1: Thermodynamics",
                "Chapter 2: Electrostatics",
                "Chapter 3: Current Electricity",
                "Chapter 4: Magnetic Effects of Electric Current",
                "Chapter 5: Electromagnetic Induction & Alternating Current",
                "Chapter 6: Geometrical Optics",
                "Chapter 7: Physical Optics",
                "Chapter 8: Introduction to Modern Physics",
                "Chapter 9: Atomic Model & Nuclear Physics",
                "Chapter 10: Semiconductor & Electronics"
            )
        ),
        SubjectInfo(
            id = "chemistry_1",
            name = "Chemistry — 1st Paper",
            group = "SCIENCE",
            papers = listOf("1st Paper"),
            defaultChapters = listOf(
                "Chapter 1: Safe Use of Laboratory",
                "Chapter 2: Qualitative Chemistry",
                "Chapter 3: Periodic Properties & Chemical Bonding",
                "Chapter 4: Chemical Changes & Equilibrium",
                "Chapter 5: Working Chemistry"
            )
        ),
        SubjectInfo(
            id = "chemistry_2",
            name = "Chemistry — 2nd Paper",
            group = "SCIENCE",
            papers = listOf("2nd Paper"),
            defaultChapters = listOf(
                "Chapter 1: Environmental Chemistry",
                "Chapter 2: Organic Chemistry",
                "Chapter 3: Quantitative Chemistry",
                "Chapter 4: Electrochemistry",
                "Chapter 5: Economic Chemistry"
            )
        ),
        SubjectInfo(
            id = "higher_math_1",
            name = "Higher Mathematics — 1st Paper",
            group = "SCIENCE",
            papers = listOf("1st Paper"),
            defaultChapters = listOf(
                "Chapter 1: Matrix & Determinants",
                "Chapter 2: Vectors",
                "Chapter 3: Straight Lines",
                "Chapter 4: Circles",
                "Chapter 5: Permutation & Combination",
                "Chapter 6: Trigonometric Ratios of Associated Angles",
                "Chapter 7: Inverse Trigonometric Functions",
                "Chapter 8: Function & Graphs",
                "Chapter 9: Differentiation (Calculus)",
                "Chapter 10: Integration (Calculus)"
            )
        ),
        SubjectInfo(
            id = "higher_math_2",
            name = "Higher Mathematics — 2nd Paper",
            group = "SCIENCE",
            papers = listOf("2nd Paper"),
            defaultChapters = listOf(
                "Chapter 1: Real Numbers & Inequalities",
                "Chapter 2: Linear Programming",
                "Chapter 3: Complex Numbers",
                "Chapter 4: Polynomials & Polynomial Equations",
                "Chapter 5: Binomial Expansion",
                "Chapter 6: Conics (Parabola, Ellipse, Hyperbola)",
                "Chapter 7: Inverse Trigonometric Functions & Equations",
                "Chapter 8: Statics",
                "Chapter 9: Dynamics of Particles",
                "Chapter 10: Measure of Dispersion & Probability"
            )
        ),
        SubjectInfo(
            id = "biology_1",
            name = "Biology — 1st Paper (Botany)",
            group = "SCIENCE",
            papers = listOf("1st Paper"),
            defaultChapters = listOf(
                "Chapter 1: Cell & Its Structure",
                "Chapter 2: Cell Division",
                "Chapter 3: Cell Chemistry",
                "Chapter 4: Microorganisms (Virus, Bacteria)",
                "Chapter 5: Algae and Fungi",
                "Chapter 6: Bryophyta and Pteridophyta",
                "Chapter 7: Gymnosperm and Angiosperm",
                "Chapter 8: Tissue and Tissue System",
                "Chapter 9: Plant Physiology",
                "Chapter 10: Plant Reproduction",
                "Chapter 11: Biotechnology",
                "Chapter 12: Organism and Environment"
            )
        ),
        SubjectInfo(
            id = "biology_2",
            name = "Biology — 2nd Paper (Zoology)",
            group = "SCIENCE",
            papers = listOf("2nd Paper"),
            defaultChapters = listOf(
                "Chapter 1: Animal Diversity & Classification",
                "Chapter 2: Animal Identification (Hydra, Grasshopper, Rohu)",
                "Chapter 3: Human Physiology — Digestion & Absorption",
                "Chapter 4: Blood & Circulation",
                "Chapter 5: Respiration & Breathing",
                "Chapter 6: Excretion & Osmoregulation",
                "Chapter 7: Locomotion & Body Movement",
                "Chapter 8: Coordination & Control",
                "Chapter 9: Human Continuation (Reproduction)",
                "Chapter 10: Human Defense (Immunity)",
                "Chapter 11: Genetics & Evolution",
                "Chapter 12: Animal Behavior"
            )
        )
    )

    // Business Studies Subjects
    val businessSubjects = listOf(
        SubjectInfo("accounting_1", "Accounting — 1st Paper", "BUSINESS_STUDIES", listOf("1st Paper"),
            listOf("Introduction to Accounting", "Accounting Equation & Transactions", "Journal & Ledger", "Cash Book", "Bank Reconciliation", "Trial Balance", "Work Sheet", "Financial Statements of Sole Proprietorship")),
        SubjectInfo("accounting_2", "Accounting — 2nd Paper", "BUSINESS_STUDIES", listOf("2nd Paper"),
            listOf("Accounts of Non-trading Concerns", "Partnership Accounts", "Joint Stock Company Capital", "Debentures", "Financial Statement Analysis", "Cost Accounting", "Management Accounting")),
        SubjectInfo("business_org_1", "Business Organization and Management — 1st Paper", "BUSINESS_STUDIES", listOf("1st Paper"),
            listOf("Fundamental Concepts of Business", "Sole Proprietorship", "Partnership Business", "Joint Stock Companies", "Cooperative Societies", "State Enterprises", "E-Business")),
        SubjectInfo("business_org_2", "Business Organization and Management — 2nd Paper", "BUSINESS_STUDIES", listOf("2nd Paper"),
            listOf("Principles of Management", "Planning", "Organizing", "Staffing & Human Resources", "Directing & Leadership", "Motivation", "Communication", "Controlling")),
        SubjectInfo("finance_1", "Finance, Banking and Insurance — 1st Paper", "BUSINESS_STUDIES", listOf("1st Paper"),
            listOf("Introduction to Finance", "Financial Environment", "Time Value of Money", "Risk and Return", "Short-term & Long-term Financing", "Capital Budgeting")),
        SubjectInfo("finance_2", "Finance, Banking and Insurance — 2nd Paper", "BUSINESS_STUDIES", listOf("2nd Paper"),
            listOf("Banking System & Central Bank (Bangladesh Bank)", "Commercial Banking", "Electronic & Modern Banking", "Bank Accounts & Negotiable Instruments", "Insurance Principles & Life Insurance")),
        SubjectInfo("marketing_1", "Production Management and Marketing — 1st Paper", "BUSINESS_STUDIES", listOf("1st Paper"),
            listOf("Production Concepts", "Production Planning & Design", "Plant Location", "Total Quality Management (TQM)", "Work Study")),
        SubjectInfo("marketing_2", "Production Management and Marketing — 2nd Paper", "BUSINESS_STUDIES", listOf("2nd Paper"),
            listOf("Marketing Environment", "Consumer Behavior", "Market Segmentation", "Product & Brand Strategy", "Pricing & Distribution Channels")),
        SubjectInfo("economics_bus_1", "Economics — 1st Paper", "BUSINESS_STUDIES", listOf("1st Paper"),
            listOf("Basic Economic Problems", "Consumer & Producer Behavior", "Demand & Supply", "Market Equilibrium", "Production and Cost")),
        SubjectInfo("economics_bus_2", "Economics — 2nd Paper", "BUSINESS_STUDIES", listOf("2nd Paper"),
            listOf("Bangladesh Economy Overview", "Agriculture Sector", "Industrial Development", "Government Finance & Inflation"))
    )

    // Humanities Subjects
    val humanitiesSubjects = listOf(
        SubjectInfo("economics_hum_1", "Economics — 1st Paper", "HUMANITIES", listOf("1st Paper"),
            listOf("Basic Economic Problems", "Consumer & Producer Behavior", "Demand & Supply", "Market Equilibrium", "Production and Cost")),
        SubjectInfo("economics_hum_2", "Economics — 2nd Paper", "HUMANITIES", listOf("2nd Paper"),
            listOf("Bangladesh Economy Overview", "Agriculture Sector", "Industrial Development", "Government Finance & Inflation")),
        SubjectInfo("civics_1", "Civics and Good Governance — 1st Paper", "HUMANITIES", listOf("1st Paper"),
            listOf("Concept of Civics & Good Governance", "Citizen Rights & Duties", "Rule of Law & Freedom", "Constitution", "Forms of Government")),
        SubjectInfo("civics_2", "Civics and Good Governance — 2nd Paper", "HUMANITIES", listOf("2nd Paper"),
            listOf("British Rule in India (1757-1947)", "Language Movement 1952", "Liberation War of Bangladesh 1971", "Constitution of Bangladesh")),
        SubjectInfo("logic_1", "Logic — 1st Paper", "HUMANITIES", listOf("1st Paper"),
            listOf("Nature of Logic", "Terms and Propositions", "Opposition of Propositions", "Deduction and Syllogism")),
        SubjectInfo("logic_2", "Logic — 2nd Paper", "HUMANITIES", listOf("2nd Paper"),
            listOf("Induction and Its Kinds", "Causality & Mill's Methods", "Scientific Hypothesis", "Fallacies")),
        SubjectInfo("history_1", "History — 1st Paper", "HUMANITIES", listOf("1st Paper"),
            listOf("Establishment of British Rule in Bengal", "Resistance & Freedom Movements", "Partition of Bengal 1905", "Lahore Resolution 1940")),
        SubjectInfo("history_2", "History — 2nd Paper", "HUMANITIES", listOf("2nd Paper"),
            listOf("Industrial Revolution", "French Revolution", "First World War & League of Nations", "Second World War & United Nations")),
        SubjectInfo("sociology_1", "Sociology — 1st Paper", "HUMANITIES", listOf("1st Paper"),
            listOf("Origin & Development of Sociology", "Social Institutions: Family & Marriage", "Culture & Civilization", "Social Stratification")),
        SubjectInfo("sociology_2", "Sociology — 2nd Paper", "HUMANITIES", listOf("2nd Paper"),
            listOf("Social History of Bengal", "Rural & Urban Society in Bangladesh", "Social Problems in Bangladesh", "Social Change"))
    )

    fun getSubjectsForGroup(group: HscGroup): List<SubjectInfo> {
        val groupList = when (group) {
            HscGroup.SCIENCE -> scienceSubjects
            HscGroup.BUSINESS_STUDIES -> businessSubjects
            HscGroup.HUMANITIES -> humanitiesSubjects
        }
        return compulsorySubjects + groupList
    }

    fun getAllSubjects(): List<SubjectInfo> {
        return compulsorySubjects + scienceSubjects + businessSubjects + humanitiesSubjects
    }

    fun findChapters(subjectName: String): List<String> {
        val cleanName = subjectName.trim().lowercase()
        val found = getAllSubjects().find { 
            it.name.lowercase().contains(cleanName) || cleanName.contains(it.name.lowercase()) 
        }
        return found?.defaultChapters ?: listOf(
            "Chapter 1: Introduction",
            "Chapter 2: Fundamental Concepts",
            "Chapter 3: Core Theory",
            "Chapter 4: Board Questions Analysis",
            "Chapter 5: Test Paper Solve"
        )
    }
}
