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
            <h5 class="card-title fw-semibold mb-4">Participants</h5>
            {{-- view model --}}
            <div class="table-responsive">
                <table class="table text-nowrap mb-0 align-middle">
                    <thead class="text-dark fs-4">
                        <tr>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">ID</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Username</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">First Name</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Last Name</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Email</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">DOB</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Image</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">School</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0 text-center">Action</h6>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        @if (count($participants) === 0)
                        <tr class="text-center">
                            <td class="border-bottom-0" colspan="9">
                                <h6 class="fw-normal mb-0">No participants found</h6>
                            </td>
                        </tr>
                        @endif
                        @foreach ($participants as $participant)
                        <tr>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $participant->id }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $participant->username }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $participant->firstname }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <p class="mb-0 fw-normal">{{ $participant->lastname }}</p>
                            </td>
                            <td class="border-bottom-0">
                                <p class="mb-0 fw-normal">{{ $participant->email }}</p>
                            </td>
                            <td class="border-bottom-0">
                                <p class="mb-0 fw-normal">{{ $participant->date_of_birth }}</p>
                            </td>
                            <td class="border-bottom-0">
                                <p class="mb-0 fw-normal">{{ $participant->image_path}}</p>
                            </td>
                            <td class="border-bottom-0">
                                <p class="mb-0 fw-normal">{{ $participant->school_registration_number }}</p>
                            </td>

                            <td class="border-bottom-0">
                                <p class="fw-bold mb-0 text-center">
                                    <span class="text-success" data-bs-toggle="modal" data-bs-target="#exampleModalview{{ $representative->representative_id }}">
                                        <b><i class="ti ti-eye"> view</i></b>
                                    </span>
                                </p>
                            </td>

                            <!-- view Modal -->
                            <div class="modal fade" id="exampleModalview{{ $participant->representative_id}}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel{{ $participant->representative_id }}">Representative Details
                                            </h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p><span class="text-primary">ID: </span>{{ $participant->representative_id }}</p>
                                            <p><span class="text-primary">Name: </span>{{ $participant->name}}
                                            </p>
                                            <p><span class="text-primary">Email:
                                                </span>{{ $participant->email }}</p>
                                            <p><span class="text-primary">School:
                                                </span>{{ $participant->school->name }}</p>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-primary btn-sm" data-bs-dismiss="modal">Close</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            {{-- end view model --}}
                        </tr>
                        @endforeach

                    </tbody>
                </table>
            </div>
        </div>
    </div>

</div>
@endsection


