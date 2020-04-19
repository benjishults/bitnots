package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.theory.DomainCategory

enum class TptpDomain(override val field: String, override val subfield: String) : DomainCategory{
    COL("Logic", "Combinatory Logic"),
    LCL("Logic", "Logic Calculi"),
    HEN("Logic", "Henkin Models"),
    SET("Mathematics", "Set Theory"),
    SEU("Mathematics", "Set Theory Continued 1"),
    SEV("Mathematics", "Set Theory Continued 2"),
    GRA("Mathematics", "Graph Theory"),
    REL("Mathematics", "Relation Algebra"),
    BOO("Mathematics", "Boolean Algebra"),
    ROB("Mathematics", "Robbins Algebra"),
    LDA("Mathematics", "Left Distributive"),
    LAT("Mathematics", "Lattices"),
    QUA("Mathematics", "Quantiles"),
    KLE("Mathematics", "Kleene Algebra"),
    GRP("Mathematics", "Groups"),
    RNG("Mathematics", "Rings"),
    FLD("Mathematics", "Fields"),
    LIN("Mathematics", "Linear Algebra"),
    HAL("Mathematics", "Homological Alg"),
    RAL("Mathematics", "Real Algebra"),
    ALG("Mathematics", "General Algebra"),
    NUM("Mathematics", "Number Theory"),
    NUN("Mathematics", "Number Theory Continued 1"),
    TOP("Mathematics", "Topology"),
    ANA("Mathematics", "Analysis"),
    GEO("Mathematics", "Geometry"),
    CAT("Mathematics", "Category Theory"),
    COM("Computer Science", "Computing Theory"),
    KRS("Computer Science", "Knowledge Representation"),
    NLP("Computer Science", "Natural Language Processing"),
    PLA("Computer Science", "Planning"),
    AGT("Computer Science", "Agents"),
    SWB("Computer Science", "Semantic Web"),
    CSR("Computer Science", "Commonsense Reasoning"),
    DAT("Computer Science", "Data Structures"),
    SWC("Computer Science", "Software Creation"),
    SWV("Computer Science", "Software Verification"),
    SWW("Computer Science", "Software Verification Continued 1"),
    BIO("Science and Engineering", "Biology"),
    HWC("Science and Engineering", "Hardware Creation"),
    HWV("Science and Engineering", "Hardware Verification"),
    MED("Science and Engineering", "Medicine"),
    PRO("Science and Engineering", "Processes"),
    PRD("Science and Engineering", "Products"),
    SCT("Social Sciences", "Social Choice Theory"),
    MGT("Social Sciences", "Management"),
    GEG("Social Sciences", "Geography"),
    PHI("Arts and Humanities", "Philosophy"),
    ARI("Other", "Arithmetic"),
    SYN("Other", "Syntactic"),
    SYO("Other", "Syntactic Continued 1"),
    PUZ("Other", "Puzzles"),
    MSC("Other", "Miscellaneous");

    override val abbreviation: String = name

    companion object {
        fun findBySubfield(subfield: String) = values().find { it.subfield == subfield }
    }
}
