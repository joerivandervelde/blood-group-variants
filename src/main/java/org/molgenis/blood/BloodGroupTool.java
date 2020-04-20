package org.molgenis.blood;

import htsjdk.tribble.readers.TabixReader;
import net.sf.samtools.util.BlockCompressedInputStream;
import org.molgenis.genotype.Allele;
import org.molgenis.vcf.VcfReader;
import org.molgenis.vcf.VcfRecord;
import org.molgenis.vcf.VcfSample;
import org.molgenis.vcf.meta.VcfMeta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static org.molgenis.blood.GenomeBuild.b37;
import static org.molgenis.blood.GenomeBuild.b38;

public class BloodGroupTool {

    private File referenceData;
    private GenomeBuild gb;
    private File GZVCFInput;
    private File output;

    public BloodGroupTool(File referenceData, GenomeBuild gb, File GZVCFInput, File output)
    {
        this.referenceData = referenceData;
        this.gb = gb;
        this.GZVCFInput = GZVCFInput;
        this.output = output;
    }

    public void run() throws Exception {

        Map<String, Set<String>> sampleToAntigens = new HashMap<>();
        List<String> sampleNames = new ArrayList<>();

        BloodVariantDataLoader bvdl = new BloodVariantDataLoader(this.referenceData);
        bvdl.load();
        List<BloodVariantData> bvd = bvdl.retrieve();

        BlockCompressedInputStream is = new BlockCompressedInputStream(this.GZVCFInput);
        VcfReader r = new VcfReader(is);
        VcfMeta vm = r.getVcfMeta();

        // init output
        for(String sample: vm.getSampleNames()){
            sampleToAntigens.put(sample, new HashSet<>());
            sampleNames.add(sample);
        }

        TabixReader t = new TabixReader(this.GZVCFInput.getAbsolutePath());

        for(BloodVariantData b : bvd)
        {
            String pos = gb == b37 ? b.hg19_pos : gb == b38 ? b.b38_pos : null;
            String chr = gb == b37 ? b.hg19_chrom : gb == b38 ? b.b38_chrom : null;
            if(pos == null || chr == null)
            {
                throw new Exception("Genome build not supported: " + gb);
            }
            String chrPos = chr + ":" + pos;
            String tabixQuery = chrPos + "-" + pos;

            TabixReader.Iterator tri = t.query(tabixQuery);
            String nextVcfLine;
            VcfRecord vr = null;
            while((nextVcfLine = tri.next()) != null)
            {
                String[] split = nextVcfLine.split("\t", -1);
                if(!split[1].equals(pos))
                {
                    continue;
                }
                if(!split[3].equals(b.Ref))
                {
                    throw new Exception("Ref allele did not match for: " + chrPos);
                    // or, we can ignore and move on..
                }
                vr = new VcfRecord(vm, nextVcfLine.split("\t", -1));
            }

            if(vr == null)
            {
                System.out.println("Variant not present: " + chrPos);
                continue;
            }

            // not necessary since VCF-IO gives us the alleles as a string
            // however - checking here saves time if allele is not present!
            boolean altFound = false;
            for(int i = 0; i < vr.getAlternateAlleles().size(); i ++)
            {
                if(b.Alt.equalsIgnoreCase(vr.getAlternateAlleles().get(i).getAlleleAsString()))
                {
                    altFound = true;
                    break;
                }
            }
            if(!altFound)
            {
                System.out.println("Blood variant alt allele not in data: " + chrPos);
                continue;
            }

            // TODO verify that sample name order is guaranteed
            // TODO deal with SNP dosage?
            // TODO deal with phasing?
            Iterator<VcfSample> vi = vr.getSamples().iterator();
            int idx = 0;
            while(vi.hasNext())
            {
                for(Allele a: vi.next().getAlleles())
                {
                    if(a.getAlleleAsString().equalsIgnoreCase(b.Alt)){
                        sampleToAntigens.get(sampleNames.get(idx)).add(b.Assoc_antigen_or_pheno);
                        break;
                    }
                }
                idx++;
            }
        }

        // create output file
        FileWriter fw = new FileWriter(output);
        BufferedWriter bw = new BufferedWriter(fw);

        for(String sample : sampleToAntigens.keySet())
        {
            StringBuffer sb = new StringBuffer();
            for(String antigen : sampleToAntigens.get(sample))
            {
                if(!antigen.equalsIgnoreCase("Wild-type") && !antigen.contains("No expression")){
                    sb.append(antigen);
                    sb.append( ", ");
                }
            }
            sb.delete(sb.length()-2, sb.length());
            bw.write(sample +" has antigens: " + sb.toString() + "\n");
        }
        bw.flush();
        bw.close();
    }

}
