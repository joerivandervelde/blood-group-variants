# blood-group-variants
A tool to determine blood group antigens from next-generation sequencing data. Currently, this is a proof-of-concept. Use only for demonstrative purposes. We thank the main data source of blood-group-variants: Montemayor-Garcia C,  Karagianni P, Stiles DA, Reese EM, Smellie DA, Loy DA, Levy KY, Nwokocha M,  Bueno MU, Miller JL, Klein HG. Genomic coordinates and continental  distribution of 120 blood group variants reported by the 1000 Genomes  Project. Transfusion. 2018 Nov;58(11):2693-2704. doi: 10.1111/trf.14953.  Epub 2018 Oct 12. PubMed PMID: 30312480. 

## How to compile
Compile using Java 8+ with these dependencies:
```
com.github.samtools:htsjdk:2.21.3
org.molgenis:vcf-io:1.1.1
```

## Quick start

Download the JAR, required data, a demo file, and run.
```
wget https://github.com/joerivandervelde/blood-group-variants/releases/download/v0.0.2/blood-group-variants-0.0.2.jar
wget https://raw.githubusercontent.com/joerivandervelde/blood-group-variants/master/data/Montemayor-Garcia_2018_ST1.tsv
wget https://github.com/joerivandervelde/blood-group-variants/raw/master/data/b37demo.vcf.gz
wget https://github.com/joerivandervelde/blood-group-variants/raw/master/data/b37demo.vcf.gz.tbi
java -jar blood-group-variants-0.0.2.jar Montemayor-Garcia_2018_ST1.tsv b37 b37demo.vcf.gz b37demo_out.txt
```

Typical usage:
```
java -jar blood-group-variants.jar [reference] [genomebuild] [input vcf] [output]
```

Example usage:
```
java -jar blood-group-variants-0.0.2.jar \
path/to/data/Montemayor-Garcia_2018_ST1.tsv \
b37 \
patho/to/my/input.vcf.gz \
patho/to/my/output.txt
```

Example output:
```
HG02938 has antigens: Jka/Jkb, Doa/Dob§ , P1+/-,Pk+, KCAM, Lu18/Lu19 (Aua/Aub), Lua/Lub, Hy (DO4), Yka, Fya/Fyb, FORS1, H- in secretions, Le(a-b-)
HG00674 has antigens: Lu22 (LURC), JK weak, Doa/Dob§ , H+w in secretions, KCAM, FORS1, Jr(a-), Le(a-b-)
NA19108 has antigens: Jka/Jkb, KCAM, Lu18/Lu19 (Aua/Aub), Hy (DO4), JK weak, Fya/Fyb, Wesb(CROM9)/Wesa(CROM8), FORS1, Lan+w, H- in secretions, Le(a-b-)
HG01174 has antigens: Yta/Ytb, Fya/Fyb, P1+/-,Pk+, KCAM, H- in secretions, Le(a-b-)
NA19681 has antigens: JK weak, Doa/Dob§ , P1+/-,Pk+, FORS1, Jr(a-), Le(a-b-), Yka
```

## To do
- Check how to properly use annotations
- Report missing and negative markers
- Handle non-ASCII annotation data
- Unit and integration testing
- Proper dependency management
- Proper cmdline option parsing
- Check how to handle allele dosage
- Check how to handle wild-type annotations
- Verify that sample order is guaranteed
- Consider phasing when genotyping
- If possible, add ABO blood system
