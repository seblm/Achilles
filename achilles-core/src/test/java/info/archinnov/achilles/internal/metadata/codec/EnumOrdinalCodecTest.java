package info.archinnov.achilles.internal.metadata.codec;

import static org.fest.assertions.api.Assertions.*;

import info.archinnov.achilles.exception.AchillesTranscodingException;
import info.archinnov.achilles.internal.metadata.holder.PropertyType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class EnumOrdinalCodecTest {

    @Test
    public void should_encode_and_decode() throws Exception {
        //Given
        List<PropertyType> enumValues = Arrays.asList(PropertyType.values());
        EnumOrdinalCodec<PropertyType> codec = EnumOrdinalCodec.create(enumValues, PropertyType.class);

        //When
        Integer encoded = codec.encode(PropertyType.COUNTER);
        PropertyType decoded = codec.decode(PropertyType.PARTITION_KEY.ordinal());

        //Then
        assertThat(encoded).isEqualTo(PropertyType.COUNTER.ordinal());
        assertThat(decoded).isSameAs(PropertyType.PARTITION_KEY);
    }

    @Test(expected = AchillesTranscodingException.class)
    public void should_exception_if_no_enum_value_match() throws Exception {
        //Given
        List<PropertyType> enumValues = Arrays.asList(PropertyType.values());
        EnumOrdinalCodec<PropertyType> codec = EnumOrdinalCodec.create(enumValues, PropertyType.class);

        //When
        codec.decode(30);
    }

}