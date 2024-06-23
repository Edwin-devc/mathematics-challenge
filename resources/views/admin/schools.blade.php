@extends('admin.layout.layout')
@section('content')
    <div class="container-fluid">
        @if (session('success'))
            <div class="alert alert-success text-center alert-dismissible fade show" role="alert">
                {{ session('success') }}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif
        <!--  Row 1 -->
        <div class="card w-100">
            <div class="card-body p-4">
                <h5 class="card-title fw-semibold mb-4">Schools</h5>
                <button type="button" class="btn btn-secondary text-white btn-sm" data-bs-toggle="modal"
                    data-bs-target="#exampleModal" data-bs-whatever="@mdo"><span><i class="ti ti-plus"></i></span>
                    Upload Schools</button>

                {{-- form modal --}}
                <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5" id="exampleModalLabel">Upload Schools</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form method="POST" action="{{ route('schools.upload') }}" enctype="multipart/form-data">
                                    @csrf
                                    <div class="row g-3">
                                        <div class="col-12">
                                            <input type="file" class="form-control" id="file" name="file"
                                                required />
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-danger btn-sm"
                                                data-bs-dismiss="modal">Close</button>
                                            <button type="submit" class="btn btn-success btn-sm">Upload</button>
                                        </div>
                                    </div>
                                </form>
                            </div>

                        </div>
                    </div>
                </div>
                {{-- end form modal --}}

                {{-- view model --}}
                
                <div class="table-responsive">
                    <table class="table text-nowrap mb-0 align-middle">
                        <thead class="text-dark fs-4">
                            <tr>
                                <th class="border-bottom-0">
                                    <h6 class="fw-semibold mb-0">NO</h6>
                                </th>
                                <th class="border-bottom-0">
                                    <h6 class="fw-semibold mb-0">Name</h6>
                                </th>
                                <th class="border-bottom-0">
                                    <h6 class="fw-semibold mb-0">District</h6>
                                </th>
                                <th class="border-bottom-0">
                                    <h6 class="fw-semibold mb-0">Registration Number</h6>
                                </th>
                                <th class="border-bottom-0">
                                    <h6 class="fw-semibold mb-0">Representative Email</h6>
                                </th>
                                <th class="border-bottom-0">
                                    <h6 class="fw-semibold mb-0">Representative Name</h6>
                                </th>
                                <th class="border-bottom-0">
                                    <h6 class="fw-semibold mb-0 text-center">Action</h6>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            @if (count($schools) === 0)
                                <tr class="text-center">
                                    <td class="border-bottom-0" colspan="7">
                                        <h6 class="fw-normal mb-0">No schools found</h6>
                                    </td>
                                </tr>
                            @endif
                            @php
                                $i = 1;
                            @endphp
                            @foreach ($schools as $school)
                                <tr>
                                    <td class="border-bottom-0">
                                        <h6 class="fw-normal mb-0">{{ $i++ }}</h6>
                                    </td>
                                    <td class="border-bottom-0">
                                        <h6 class="fw-normal mb-0">{{ $school->name }}</h6>
                                    </td>
                                    <td class="border-bottom-0">
                                        <h6 class="fw-normal mb-0">{{ $school->district }}</h6>
                                    </td>
                                    <td class="border-bottom-0">
                                        <p class="mb-0 fw-normal">{{ $school->registration_number }}</p>
                                    </td>
                                    <td class="border-bottom-0">
                                        <p class="fw-normal mb-0">{{ $school->representative_email }}</p>
                                    </td>
                                    <td class="border-bottom-0">
                                        <p class="fw-normal mb-0">{{ $school->representative_name }}</p>
                                    </td>

                                    <td class="border-bottom-0">
                                        <p class="fw-bold mb-0 fs-4">
                                            <span class="mx-2 text-success" data-bs-toggle="modal"
                                                data-bs-target="#exampleModalview{{ $school->school_id }}">
                                                <i class="ti ti-eye"></i>
                                            </span>

                                            <span class="me-2 text-primary" data-bs-toggle="modal"
                                                data-bs-target="#exampleModaledit{{ $school->school_id }}" data-bs-whatever="@mdo">
                                                <i class="ti ti-edit"></i>
                                            </span>

                                            <span class="me-2 text-primary" data-bs-toggle="modal"
                                                data-bs-target="#exampleModaldelete{{ $school->school_id }}" data-bs-whatever="@mdo">
                                                <i class="ti ti-trash text-danger"></i>
                                            </span>

                                        </p>
                                    </td>

                                    <!-- view Modal -->
                                    <div class="modal fade" id="exampleModalview{{ $school->school_id }}" tabindex="-1"
                                        aria-labelledby="exampleModalLabel" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5"
                                                        id="exampleModalLabel{{ $school->school_id }}">School Details
                                                    </h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                        aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <p><span class="text-primary">Name: </span>{{ $school->name }}</p>
                                                    <p><span class="text-primary">District: </span>{{ $school->district }}
                                                    </p>
                                                    <p><span class="text-primary">Reg No:
                                                        </span>{{ $school->registration_number }}</p>
                                                    <p><span class="text-primary">Rep Email:
                                                        </span>{{ $school->representative_email }}</p>
                                                    <p><span class="text-primary">Rep Name:
                                                        </span>{{ $school->representative_name }}</p>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-primary btn-sm"
                                                        data-bs-dismiss="modal">Close</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    {{-- end view model --}}


                                    {{-- edit form modal --}}
                                    <div class="modal fade" id="exampleModaledit{{ $school->school_id }}" tabindex="-1"
                                        aria-labelledby="exampleModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Update School Details
                                                    </h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                        aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <form method="POST" action="{{ route('admin.schools.update', ['school_id' => $school->school_id]) }}">
                                                        @csrf
                                                        @method('PUT')
                                                        <div class="row g-3">
                                                            <div class="col-md-6">
                                                                <div class="form-floating">
                                                                    <input type="text" class="form-control"
                                                                        id="name" name="name" placeholder="Name"
                                                                        value="{{ $school->name }}" required />
                                                                    <label for="name">Name</label>
                                                                </div>
                                                            </div>

                                                            <div class="col-md-6">
                                                                <div class="form-floating">
                                                                    <input type="text" class="form-control"
                                                                        id="contact" name="district" value="{{ $school->district }}"
                                                                        placeholder="District" required />
                                                                    <label for="district">District</label>
                                                                </div>
                                                            </div>

                                                            <div class="col-md-6">
                                                                <div class="form-floating">
                                                                    <input type="text"
                                                                        class="form-control"
                                                                        id="registration_number" placeholder="Registration Number"
                                                                        name="registration_number" value="{{ $school->registration_number }}" required />
                                                                    <label for="registration_number">Registration Number</label>
                                                                </div>
                                                            </div>

                                                            <div class="col-md-6">
                                                                <div class="form-floating">
                                                                    <input type="email" class="form-control"
                                                                        id="email" name="representative_email" placeholder="Email"
                                                                        value="{{ $school->representative_email }}" required />
                                                                    <label for="email">Representative Email</label>
                                                                </div>
                                                            </div>
                                                            
                                                            <div class="col-12">
                                                                <div class="form-floating">
                                                                    <input type="text"
                                                                        class="form-control"
                                                                        id="representative_name" placeholder="Representative Name"
                                                                        name="representative_name" value="{{ $school->representative_name }}"
                                                                        required />
                                                                    <label for="representative_name">Representative Name</label>
                                                                </div>
                                                            </div>
                                                            
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-danger btn-sm"
                                                                    data-bs-dismiss="modal">Close</button>
                                                                <button type="submit"
                                                                    class="btn btn-success btn-sm">Update</button>
                                                            </div>
                                                        </div>
                                                    </form>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                    {{-- end edit form modal --}}

                                    {{-- delete modal --}}
                                    <div class="modal fade" id="exampleModaldelete{{ $school->school_id }}" tabindex="-1"
                                        aria-labelledby="exampleModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Delete School
                                                    </h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                        aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Are you sure you want to delete {{ $school->name }}?</p>
                                                    <form action="{{ route('admin.schools.delete', ['school_id' => $school->school_id]) }}" method="POST" style="display:inline;"
                                                        class="delete-form">
                                                        @csrf
                                                        @method('DELETE')
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-sm btn-success"
                                                                data-bs-dismiss="modal">No</button>
                                                            <button type="submit"
                                                                class="btn btn-sm btn-danger">Yes</button>
                                                        </div>

                                                    </form>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                    {{-- end delete modal --}}

                                    {{-- mark complete modal --}}
                                    <div class="modal fade" id="exampleModalcomplete" tabindex="-1"
                                        aria-labelledby="exampleModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Mark Complete
                                                    </h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                        aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Are you sure you want to mark this booking as complete?</p>
                                                    <form action="" method="POST" style="display:inline;"
                                                        class="delete-form">
                                                        @csrf
                                                        @method('PUT')
                                                        <input type="hidden" name="status" value="Closed">
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-sm btn-success"
                                                                data-bs-dismiss="modal">No</button>
                                                            <button type="submit"
                                                                class="btn btn-sm btn-danger">Yes</button>
                                                        </div>

                                                    </form>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                    {{-- end mark complete modal --}}
                                </tr>
                            @endforeach

                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- end Row 1 -->


        {{-- <div class="card">
            <div class="card-body">
                <h5 class="card-title fw-semibold mb-4">Bookings</h5>
                <div class="card">
                    <div class="card-body p-4">

                    </div>
                </div>
                <div class="card">
                    <div class="card-body p-4">
                        <button type="button" class="btn btn-primary m-1">Primary</button>
                        <button type="button" class="btn btn-secondary m-1">Secondary</button>
                        <button type="button" class="btn btn-success m-1">Success</button>
                        <button type="button" class="btn btn-danger m-1">Danger</button>
                        <button type="button" class="btn btn-warning m-1">Warning</button>
                        <button type="button" class="btn btn-info m-1">Info</button>
                        <button type="button" class="btn btn-light m-1">Light</button>
                        <button type="button" class="btn btn-dark m-1">Dark</button>
                        <button type="button" class="btn btn-link m-1">Link</button>
                    </div>
                </div>
                <h5 class="card-title fw-semibold mb-4">Outline buttons</h5>
                <div class="card mb-0">
                    <div class="card-body p-4">
                        <button type="button" class="btn btn-outline-primary m-1">Primary</button>
                        <button type="button" class="btn btn-outline-secondary m-1">Secondary</button>
                        <button type="button" class="btn btn-outline-success m-1">Success</button>
                        <button type="button" class="btn btn-outline-danger m-1">Danger</button>
                        <button type="button" class="btn btn-outline-warning m-1">Warning</button>
                        <button type="button" class="btn btn-outline-info m-1">Info</button>
                        <button type="button" class="btn btn-outline-light m-1">Light</button>
                        <button type="button" class="btn btn-outline-dark m-1">Dark</button>
                        <button type="button" class="btn btn-outline-link m-1">Link</button>
                    </div>
                </div>
            </div>
        </div> --}}

    </div>
@endsection
